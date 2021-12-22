package com.savaliscodes.mydiary
//simply initialise all data you need from the database
//NB must be nullable
data class DiaryData(var DiaryLogID: String ?= null, var UserId: String ?= null,
                     var LogTitle:String ?= null, var LogContents: String ?= null,
                     var LogTime: String ?= null)
