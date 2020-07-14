package com.test.dynamicdemo.model

data class SurveyData(
    val answerMast: List<AnswerMast>,
    val controlType: String,
    val keyboardType: String,
    val qSortOrder: String,
    val qid: String,
    val question: String,
    val regularExpression: String,
    val requiredValidator: String,
    val requiredValidatorMessage: String
)