package com.example.shoppingapp.domain.model.ghn

data class DistrictGhn(val districtID: Int, val provinceID: Int, val districtName: String) :
    AddressGhn()