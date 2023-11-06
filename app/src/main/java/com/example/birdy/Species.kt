package com.example.birdy

import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("sciName") var scientific_name: String? = null,
    @SerializedName("comName") var common_name: String? = null,
    @SerializedName("speciesCode") var species_code: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("taxonOrder") var taxon_order: String? = null,
    @SerializedName("comNameCodes") var com_name_codes: Array<String>? = null,
    @SerializedName("sciNameCodes") var sci_name_codes: Array<String>?  = null,
    @SerializedName("bandingCodes") var banding_codes: Array<String>? = null,
    @SerializedName("order") var order: String? = null,
    @SerializedName("familyCode") var family_code: String? = null,
    @SerializedName("familyComName") var family_com_name: String? = null,
    @SerializedName("familySciName") var family_sci_name: String? = null
)
