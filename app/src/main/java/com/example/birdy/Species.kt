package com.example.birdy

import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("SCIENTIFIC_NAME") var scientific_name: String? = null,
    @SerializedName("COMMON_NAME") var common_name: String? = null,
    @SerializedName("SPECIES_CODE") var species_code: String? = null,
    @SerializedName("CATEGORY") var category: String? = null,
    @SerializedName("TAXON_ORDER") var taxon_order: String? = null,
    @SerializedName("COM_NAME_CODES") var com_name_codes: String? = null,
    @SerializedName("SCI_NAME_CODES") var sci_name_codes: String? = null,
    @SerializedName("BANDING_CODES") var banding_codes: String? = null,
    @SerializedName("ORDER") var order: String? = null,
    @SerializedName("FAMILY_COM_NAME") var family_com_name: String? = null,
    @SerializedName("FAMILY_SCI_NAME") var family_sci_name: String? = null,
    @SerializedName("REPORT_AS") var report_as: String? = null,
    @SerializedName("EXTINCT") var extinct: Any? = null,
    @SerializedName("EXTINCT_YEAR") var extinct_year: Any? = null
)
