package no.nav.gravferd.sak.arkitektur

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.name
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import no.nav.gravferd.sak.kjerne.tilgang.TilgangService
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

class ArkitekturTest {
    @Test
    fun `avhengigheter peker mot kjernen`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("$ROT..")
            .withOptionalLayers(true)
            .layer("Oppstart")
            .definedBy(name("$ROT.Application").or(name("$ROT.ApplicationKt")))
            .layer("Kjerne")
            .definedBy("$ROT.kjerne..")
            .layer("Inn")
            .definedBy("$ROT.inn..")
            .layer("Ut")
            .definedBy("$ROT.ut..")
            .layer("Common")
            .definedBy("$ROT.common..")
            .layer("Config")
            .definedBy("$ROT.config..")
            .whereLayer("Oppstart")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Config")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Inn")
            .mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Ut")
            .mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Kjerne")
            .mayOnlyBeAccessedByLayers("Inn", "Ut", "Config")
            .check(klasser)
    }

    @Test
    fun `inn og ut inneholder ikke servicer`() {
        noClasses()
            .that()
            .resideInAnyPackage("$ROT.inn..", "$ROT.ut..")
            .should()
            .beAnnotatedWith(Service::class.java)
            .check(klasser)
    }

    @Test
    fun `all produksjonskode ligger i et kjent lag`() {
        classes()
            .that()
            .doNotHaveFullyQualifiedName("$ROT.Application")
            .and()
            .doNotHaveFullyQualifiedName("$ROT.ApplicationKt")
            .should()
            .resideInAnyPackage(
                "$ROT.kjerne..",
                "$ROT.inn..",
                "$ROT.ut..",
                "$ROT.common..",
                "$ROT.config..",
            ).check(klasser)
    }

    @Test
    fun `alle endepunkter sjekker tilgang`() {
        methods()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(RestController::class.java)
            .should(kalleTilgangssjekk())
            .check(klasser)
    }

    private companion object {
        const val ROT = "no.nav.gravferd.sak"

        private fun kalleTilgangssjekk(): ArchCondition<JavaMethod> =
            object : ArchCondition<JavaMethod>("kalle TilgangService.validerHarTilgangTilHandling") {
                override fun check(
                    metode: JavaMethod,
                    events: ConditionEvents,
                ) {
                    val kallerSjekk =
                        metode.methodCallsFromSelf.any {
                            it.targetOwner.isEquivalentTo(TilgangService::class.java) &&
                                it.name == TilgangService::validerHarTilgangTilHandling.name
                        }
                    events.add(SimpleConditionEvent(metode, kallerSjekk, "${metode.fullName} kaller ikke tilgangssjekken"))
                }
            }

        private val klasser: JavaClasses by lazy {
            ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages(ROT)
        }
    }
}
