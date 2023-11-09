package com.example.birdy.observations

import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("sciName") var scientific_name: String,
    @SerializedName("comName") var common_name: String,
    @SerializedName("speciesCode") var species_code: String,
    @SerializedName("category") var category: String,
    @SerializedName("taxonOrder") var taxon_order: String,
    @SerializedName("comNameCodes") var com_name_codes: List<String>,
    @SerializedName("sciNameCodes") var sci_name_codes: List<String>,
    @SerializedName("bandingCodes") var banding_codes: List<String>,
    @SerializedName("order") var order: String,
    @SerializedName("familyCode") var family_code: String,
    @SerializedName("familyComName") var family_com_name: String,
    @SerializedName("familySciName") var family_sci_name: String
) {
    override fun toString(): String {
        return common_name
    }
}

