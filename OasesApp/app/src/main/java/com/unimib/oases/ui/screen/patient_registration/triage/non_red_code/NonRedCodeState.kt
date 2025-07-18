package com.unimib.oases.ui.screen.patient_registration.triage.non_red_code

data class NonRedCodeState(
    var airwaySwellingMass: Boolean = false,
    var ongoingBleeding: Boolean = false,
    var severePallor: Boolean = false,
    var ongoingSevereVomitingDiarrhea: Boolean = false,
    var unableToFeedOrDrink: Boolean = false,
    var recentFainting: Boolean = false,
    var lethargyConfusionAgitation: Boolean = false,
    var focalNeurologicVisualDeficit: Boolean = false,
    var headacheWithStiffNeck: Boolean = false,
    var severePain: Boolean = false,
    var acuteTesticularScrotalPainPriapism: Boolean = false,
    var unableToPassUrine: Boolean = false,
    var acuteLimbDeformityOpenFracture: Boolean = false,
    var otherTraumaBurns: Boolean = false,
    var sexualAssault: Boolean = false,
    var animalBiteNeedlestickPuncture: Boolean = false,
    var otherPregnancyRelatedComplaints: Boolean = false,
    var ageOver80Years: Boolean = false,
    var alteredVitalSignsSpo2: Boolean = false,
    var alteredVitalSignsRrLow: Boolean = false,
    var alteredVitalSignsRrHigh: Boolean = false,
    var alteredVitalSignsHrLow: Boolean = false,
    var alteredVitalSignsHrHigh: Boolean = false,
    var alteredVitalSignsSbpLow: Boolean = false,
    var alteredVitalSignsSbpHigh: Boolean = false,
    var alteredVitalSignsTempLow: Boolean = false,
    var alteredVitalSignsTempHigh: Boolean = false,
){
    val isYellowCode: Boolean
        get() = airwaySwellingMass || ongoingBleeding || severePallor || ongoingSevereVomitingDiarrhea ||
                unableToFeedOrDrink || recentFainting || lethargyConfusionAgitation || focalNeurologicVisualDeficit
                || headacheWithStiffNeck || severePain || acuteTesticularScrotalPainPriapism || unableToPassUrine ||
                acuteLimbDeformityOpenFracture || otherTraumaBurns || sexualAssault || animalBiteNeedlestickPuncture ||
                otherPregnancyRelatedComplaints || ageOver80Years || alteredVitalSignsSpo2 || alteredVitalSignsRrLow ||
                alteredVitalSignsRrHigh || alteredVitalSignsHrLow || alteredVitalSignsHrHigh || alteredVitalSignsSbpLow ||
                alteredVitalSignsSbpHigh || alteredVitalSignsTempLow || alteredVitalSignsTempHigh
}