package am.leon.utilities.helpers.country

/**
 * Per-country phone and postal rules, keyed by ISO 3166-1 alpha-2 code.
 *
 * Built on first use so the table costs nothing until a field is validated,
 * and split across functions to keep each one well inside the JVM's 64 KB
 * per-method bytecode limit.
 */
internal val countryValidationRules: Map<String, CountryValidationRule> by lazy(LazyThreadSafetyMode.PUBLICATION) {
    HashMap<String, CountryValidationRule>(490).apply {
        putRules0()
        putRules1()
        putRules2()
        putRules3()
        putRules4()
        putRules5()
        putRules6()
        putRules7()
        putRules8()
        putRules9()
    }
}

/**
 * Country codes (ISO 3166-1 alpha-2) sharing each dial code, main country first.
 *
 * Most dial codes map to exactly one country; the exceptions (+1 across the
 * NANP, +44 across GB/GG/IM/JE, +7 across RU/KZ, ...) map to several, and a
 * number valid in any one of them has to be accepted.
 */
internal val dialCodeToCountryCodes: Map<String, List<String>> by lazy(LazyThreadSafetyMode.PUBLICATION) {
    mapOf(
        "1" to listOf(
            "US",
            "AG",
            "AI",
            "AS",
            "BB",
            "BM",
            "BS",
            "CA",
            "DM",
            "DO",
            "GD",
            "GU",
            "JM",
            "KN",
            "KY",
            "LC",
            "MP",
            "MS",
            "PR",
            "SX",
            "TC",
            "TT",
            "VC",
            "VG",
            "VI"
        ),
        "7" to listOf("RU", "KZ"),
        "20" to listOf("EG"),
        "27" to listOf("ZA"),
        "30" to listOf("GR"),
        "31" to listOf("NL"),
        "32" to listOf("BE"),
        "33" to listOf("FR"),
        "34" to listOf("ES"),
        "36" to listOf("HU"),
        "39" to listOf("IT", "VA"),
        "40" to listOf("RO"),
        "41" to listOf("CH"),
        "43" to listOf("AT"),
        "44" to listOf("GB", "GG", "IM", "JE"),
        "45" to listOf("DK"),
        "46" to listOf("SE"),
        "47" to listOf("NO", "SJ"),
        "48" to listOf("PL"),
        "49" to listOf("DE"),
        "51" to listOf("PE"),
        "52" to listOf("MX"),
        "53" to listOf("CU"),
        "54" to listOf("AR"),
        "55" to listOf("BR"),
        "56" to listOf("CL"),
        "57" to listOf("CO"),
        "58" to listOf("VE"),
        "60" to listOf("MY"),
        "61" to listOf("AU", "CC", "CX"),
        "62" to listOf("ID"),
        "63" to listOf("PH"),
        "64" to listOf("NZ"),
        "65" to listOf("SG"),
        "66" to listOf("TH"),
        "81" to listOf("JP"),
        "82" to listOf("KR"),
        "84" to listOf("VN"),
        "86" to listOf("CN"),
        "90" to listOf("TR"),
        "91" to listOf("IN"),
        "92" to listOf("PK"),
        "93" to listOf("AF"),
        "94" to listOf("LK"),
        "95" to listOf("MM"),
        "98" to listOf("IR"),
        "211" to listOf("SS"),
        "212" to listOf("MA", "EH"),
        "213" to listOf("DZ"),
        "216" to listOf("TN"),
        "218" to listOf("LY"),
        "220" to listOf("GM"),
        "221" to listOf("SN"),
        "222" to listOf("MR"),
        "223" to listOf("ML"),
        "224" to listOf("GN"),
        "225" to listOf("CI"),
        "226" to listOf("BF"),
        "227" to listOf("NE"),
        "228" to listOf("TG"),
        "229" to listOf("BJ"),
        "230" to listOf("MU"),
        "231" to listOf("LR"),
        "232" to listOf("SL"),
        "233" to listOf("GH"),
        "234" to listOf("NG"),
        "235" to listOf("TD"),
        "236" to listOf("CF"),
        "237" to listOf("CM"),
        "238" to listOf("CV"),
        "239" to listOf("ST"),
        "240" to listOf("GQ"),
        "241" to listOf("GA"),
        "242" to listOf("CG"),
        "243" to listOf("CD"),
        "244" to listOf("AO"),
        "245" to listOf("GW"),
        "246" to listOf("IO"),
        "247" to listOf("AC"),
        "248" to listOf("SC"),
        "249" to listOf("SD"),
        "250" to listOf("RW"),
        "251" to listOf("ET"),
        "252" to listOf("SO"),
        "253" to listOf("DJ"),
        "254" to listOf("KE"),
        "255" to listOf("TZ"),
        "256" to listOf("UG"),
        "257" to listOf("BI"),
        "258" to listOf("MZ"),
        "260" to listOf("ZM"),
        "261" to listOf("MG"),
        "262" to listOf("RE", "YT"),
        "263" to listOf("ZW"),
        "264" to listOf("NA"),
        "265" to listOf("MW"),
        "266" to listOf("LS"),
        "267" to listOf("BW"),
        "268" to listOf("SZ"),
        "269" to listOf("KM"),
        "290" to listOf("SH", "TA"),
        "291" to listOf("ER"),
        "297" to listOf("AW"),
        "298" to listOf("FO"),
        "299" to listOf("GL"),
        "350" to listOf("GI"),
        "351" to listOf("PT"),
        "352" to listOf("LU"),
        "353" to listOf("IE"),
        "354" to listOf("IS"),
        "355" to listOf("AL"),
        "356" to listOf("MT"),
        "357" to listOf("CY"),
        "358" to listOf("FI", "AX"),
        "359" to listOf("BG"),
        "370" to listOf("LT"),
        "371" to listOf("LV"),
        "372" to listOf("EE"),
        "373" to listOf("MD"),
        "374" to listOf("AM"),
        "375" to listOf("BY"),
        "376" to listOf("AD"),
        "377" to listOf("MC"),
        "378" to listOf("SM"),
        "380" to listOf("UA"),
        "381" to listOf("RS"),
        "382" to listOf("ME"),
        "383" to listOf("XK"),
        "385" to listOf("HR"),
        "386" to listOf("SI"),
        "387" to listOf("BA"),
        "389" to listOf("MK"),
        "420" to listOf("CZ"),
        "421" to listOf("SK"),
        "423" to listOf("LI"),
        "500" to listOf("FK"),
        "501" to listOf("BZ"),
        "502" to listOf("GT"),
        "503" to listOf("SV"),
        "504" to listOf("HN"),
        "505" to listOf("NI"),
        "506" to listOf("CR"),
        "507" to listOf("PA"),
        "508" to listOf("PM"),
        "509" to listOf("HT"),
        "590" to listOf("GP", "BL", "MF"),
        "591" to listOf("BO"),
        "592" to listOf("GY"),
        "593" to listOf("EC"),
        "594" to listOf("GF"),
        "595" to listOf("PY"),
        "596" to listOf("MQ"),
        "597" to listOf("SR"),
        "598" to listOf("UY"),
        "599" to listOf("CW", "BQ"),
        "670" to listOf("TL"),
        "672" to listOf("NF"),
        "673" to listOf("BN"),
        "674" to listOf("NR"),
        "675" to listOf("PG"),
        "676" to listOf("TO"),
        "677" to listOf("SB"),
        "678" to listOf("VU"),
        "679" to listOf("FJ"),
        "680" to listOf("PW"),
        "681" to listOf("WF"),
        "682" to listOf("CK"),
        "683" to listOf("NU"),
        "685" to listOf("WS"),
        "686" to listOf("KI"),
        "687" to listOf("NC"),
        "688" to listOf("TV"),
        "689" to listOf("PF"),
        "690" to listOf("TK"),
        "691" to listOf("FM"),
        "692" to listOf("MH"),
        "850" to listOf("KP"),
        "852" to listOf("HK"),
        "853" to listOf("MO"),
        "855" to listOf("KH"),
        "856" to listOf("LA"),
        "880" to listOf("BD"),
        "886" to listOf("TW"),
        "960" to listOf("MV"),
        "961" to listOf("LB"),
        "962" to listOf("JO"),
        "963" to listOf("SY"),
        "964" to listOf("IQ"),
        "965" to listOf("KW"),
        "966" to listOf("SA"),
        "967" to listOf("YE"),
        "968" to listOf("OM"),
        "970" to listOf("PS"),
        "971" to listOf("AE"),
        "972" to listOf("IL"),
        "973" to listOf("BH"),
        "974" to listOf("QA"),
        "975" to listOf("BT"),
        "976" to listOf("MN"),
        "977" to listOf("NP"),
        "992" to listOf("TJ"),
        "993" to listOf("TM"),
        "994" to listOf("AZ"),
        "995" to listOf("GE"),
        "996" to listOf("KG"),
        "998" to listOf("UZ"),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules0() {
    put(
        key = "AC",
        value = CountryValidationRule(
            countryCode = "AC",
            dialCode = "247",
            nationalPrefix = null,
            phonePattern = "(?:[2-47][0-9]{4})",
            phoneLengths = listOf(5),
            phoneExample = "40123",
            postalPattern = "ASCN 1ZZ",
            postalExample = "ASCN 1ZZ",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AD",
        value = CountryValidationRule(
            countryCode = "AD",
            dialCode = "376",
            nationalPrefix = null,
            phonePattern = "(?:690[0-9]{6}|[356][0-9]{5})",
            phoneLengths = listOf(6, 9),
            phoneExample = "312345",
            postalPattern = "AD[1-7]0[0-9]",
            postalExample = "AD100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AE",
        value = CountryValidationRule(
            countryCode = "AE",
            dialCode = "971",
            nationalPrefix = "0",
            phonePattern = "(?:5[02-68][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "501234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "AF",
        value = CountryValidationRule(
            countryCode = "AF",
            dialCode = "93",
            nationalPrefix = "0",
            phonePattern = "(?:7[0-9]{8})",
            phoneLengths = listOf(9),
            phoneExample = "701234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AG",
        value = CountryValidationRule(
            countryCode = "AG",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:268(?:464|7(?:1[3-9]|[28][0-9]|3[0246]|64|7[0-689]))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "2684641234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "AI",
        value = CountryValidationRule(
            countryCode = "AI",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:264(?:235|4(?:69|7[67])|5(?:3[6-9]|8[1-4])|7(?:29|72))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "2642351234",
            postalPattern = "(?:AI-)?2640",
            postalExample = "2640",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AL",
        value = CountryValidationRule(
            countryCode = "AL",
            dialCode = "355",
            nationalPrefix = "0",
            phonePattern = "(?:6(?:[78][2-9]|9[0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "672123456",
            postalPattern = "[0-9]{4}",
            postalExample = "1001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AM",
        value = CountryValidationRule(
            countryCode = "AM",
            dialCode = "374",
            nationalPrefix = "0",
            phonePattern = "(?:(?:33|4[1349]|55|77|88|9[13-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "77123456",
            postalPattern = "(?:37)?[0-9]{4}",
            postalExample = "375010",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AO",
        value = CountryValidationRule(
            countryCode = "AO",
            dialCode = "244",
            nationalPrefix = null,
            phonePattern = "(?:9[1-79][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "923123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "AR",
        value = CountryValidationRule(
            countryCode = "AR",
            dialCode = "54",
            nationalPrefix = "0",
            phonePattern = "(?:93(?:7(?:1[15]|81)|8(?:21|4[16]|69|9[12]))[46][0-9]{5}|9(?:2(?:2(?:2[59]|44|52)|3(?:26|44)|47[35]|9(?:[07]2|2[26]|34|46))|3327)[45][0-9]{5}|9(?:2(?:657|9(?:54|66))|3(?:48[27]|7(?:55|77)|8(?:65|78)))[2-8][0-9]{5}|9(?:2(?:284|3(?:02|23)|477|622|920)|3(?:4(?:46|89|92)|541))[2-7][0-9]{5}|(?:675[0-9]|9(?:11[0-8][0-9]|2(?:2(?:0[45]|1[2-6]|3[3-6])|3(?:[06]4|7[45])|494|6(?:04|1[2-8]|[36][45]|4[3-6])|80[45]|9(?:[17][4-6]|[48][45]|9[3-6]))|3(?:364|4(?:1[2-8]|[25][4-6]|3[3-6]|84)|5(?:1[2-9]|[38][4-6])|6(?:2[45]|44)|7[069][45]|8(?:0[45]|1[2-7]|3[4-6]|5[3-6]|7[2-6]|8[3-68]))))[0-9]{6}|9(?:2(?:2(?:62|81)|320|9(?:42|83))|3(?:329|4(?:62|7[16])|5(?:43|64)|7(?:18|5[17])))[2-6][0-9]{5}|92(?:2(?:21|4[23]|6[145]|7[1-4]|8[356]|9[267])|3(?:16|3[13-8]|43|5[346-8]|9[3-5])|6(?:2[46]|4[78]|5[1568])|9(?:03|2[1457-9]|3[1356]|4[08]|[56][23]|82))4[0-9]{5}|9(?:2(?:257|3(?:24|46|92)|9(?:01|23|64))|3(?:4(?:42|64)|5(?:25|37|4[47]|71)|7(?:35|72)|825))[3-6][0-9]{5}|9(?:2(?:2(?:02|2[3467]|4[156]|5[45]|6[6-8]|91)|3(?:1[47]|25|[45][25]|96)|47[48]|625|932)|3(?:38[2578]|4(?:0[0-24-9]|3[78]|4[457]|58|6[035-9]|72|83|9[136-8])|5(?:2[124]|[368][23]|4[2689]|7[2-6])|7(?:16|2[15]|3[14]|4[13]|5[468]|7[3-5]|8[26])|8(?:2[67]|3[278]|4[3-5]|5[78]|6[1-378]|[78]7|94)))[4-6][0-9]{5})",
            phoneLengths = listOf(10, 11),
            phoneExample = "91123456789",
            postalPattern = "[A-HJ-NP-Z][0-9]{4}[A-Z]{3}",
            postalExample = "C1070AAM",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AS",
        value = CountryValidationRule(
            countryCode = "AS",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:684(?:2(?:48|5[2468]|7[246])|7(?:3[13]|70|82))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "6847331234",
            postalPattern = "(96799)(?:[ \\-]([0-9]{4}))?",
            postalExample = "96799",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AT",
        value = CountryValidationRule(
            countryCode = "AT",
            dialCode = "43",
            nationalPrefix = "0",
            phonePattern = "(?:6(?:485|(?:5[0-3579]|6[013-9]|[7-9][0-9])[0-9])[0-9]{3,9})",
            phoneLengths = listOf(7, 8, 9, 10, 11, 12, 13),
            phoneExample = "664123456",
            postalPattern = "[0-9]{4}",
            postalExample = "1010",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AU",
        value = CountryValidationRule(
            countryCode = "AU",
            dialCode = "61",
            nationalPrefix = "0",
            phonePattern = "(?:4(?:79[01]|83[0-36-9]|95[0-3])[0-9]{5}|4(?:[0-36][0-9]|4[047-9]|[58][0-24-9]|7[02-8]|9[0-47-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "412345678",
            postalPattern = "[0-9]{4}",
            postalExample = "2060",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AW",
        value = CountryValidationRule(
            countryCode = "AW",
            dialCode = "297",
            nationalPrefix = null,
            phonePattern = "(?:(?:290|5[69][0-9]|6(?:[03]0|22|4[0-2]|[69][0-9])|7(?:[34][0-9]|7[07])|9(?:6[45]|9[4-8]))[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "5601234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "AX",
        value = CountryValidationRule(
            countryCode = "AX",
            dialCode = "358",
            nationalPrefix = "0",
            phonePattern = "(?:4946[0-9]{2,6}|(?:4[0-8]|50)[0-9]{4,8})",
            phoneLengths = listOf(6, 7, 8, 9, 10),
            phoneExample = "412345678",
            postalPattern = "22[0-9]{3}",
            postalExample = "22150",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "AZ",
        value = CountryValidationRule(
            countryCode = "AZ",
            dialCode = "994",
            nationalPrefix = "0",
            phonePattern = "(?:36554[0-9]{4}|(?:[16]0|4[04]|5[015]|7[07]|99)[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "401234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BA",
        value = CountryValidationRule(
            countryCode = "BA",
            dialCode = "387",
            nationalPrefix = "0",
            phonePattern = "(?:6040[0-9]{5}|6(?:03|[1-356]|44|7[0-9])[0-9]{6})",
            phoneLengths = listOf(8, 9),
            phoneExample = "61123456",
            postalPattern = "[0-9]{5}",
            postalExample = "71000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BB",
        value = CountryValidationRule(
            countryCode = "BB",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:246(?:(?:2(?:[3568][0-9]|4[0-57-9])|3(?:5[2-9]|6[0-6])|4(?:46|5[0-9])|69[5-7]|8(?:[2-5][0-9]|83))[0-9]|52(?:1[147]|20))[0-9]{3})",
            phoneLengths = listOf(10),
            phoneExample = "2462501234",
            postalPattern = "BB[0-9]{5}",
            postalExample = "BB23026",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BD",
        value = CountryValidationRule(
            countryCode = "BD",
            dialCode = "880",
            nationalPrefix = "0",
            phonePattern = "(?:(?:1[13-9][0-9]|644)[0-9]{7}|(?:3[78]|44|66)[02-9][0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "1812345678",
            postalPattern = "[0-9]{4}",
            postalExample = "1340",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BE",
        value = CountryValidationRule(
            countryCode = "BE",
            dialCode = "32",
            nationalPrefix = "0",
            phonePattern = "(?:4[5-9][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "450001234",
            postalPattern = "[0-9]{4}",
            postalExample = "4000",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BF",
        value = CountryValidationRule(
            countryCode = "BF",
            dialCode = "226",
            nationalPrefix = null,
            phonePattern = "(?:(?:0[1-7]|4[4-6]|5[0-8]|[67][0-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "70123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "BG",
        value = CountryValidationRule(
            countryCode = "BG",
            dialCode = "359",
            nationalPrefix = "0",
            phonePattern = "(?:(?:43[07-9]|99[69][0-9])[0-9]{5}|(?:8[7-9]|98)[0-9]{7})",
            phoneLengths = listOf(8, 9),
            phoneExample = "43012345",
            postalPattern = "[0-9]{4}",
            postalExample = "1000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BH",
        value = CountryValidationRule(
            countryCode = "BH",
            dialCode = "973",
            nationalPrefix = null,
            phonePattern = "(?:(?:3(?:[0-79][0-9]|8[0-57-9])[0-9]|6(?:3(?:00|33|6[16])|441|6(?:3[03-9]|[69][0-9]|7[0-689])))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "36001234",
            postalPattern = "(?:^|\\b)(?:1[0-2]|[1-9])[0-9]{2}(?:\$|\\b)",
            postalExample = "317",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BI",
        value = CountryValidationRule(
            countryCode = "BI",
            dialCode = "257",
            nationalPrefix = null,
            phonePattern = "(?:(?:29|6[1-9]|7[125-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "79561234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "BJ",
        value = CountryValidationRule(
            countryCode = "BJ",
            dialCode = "229",
            nationalPrefix = null,
            phonePattern = "(?:01(?:2[5-9]|[4-69][0-9])[0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "0195123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules1() {
    put(
        key = "BL",
        value = CountryValidationRule(
            countryCode = "BL",
            dialCode = "590",
            nationalPrefix = "0",
            phonePattern = "(?:(?:69(?:0[0-9][0-9]|1(?:2[2-9]|3[0-5]))|7090[0-4])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "690001234",
            postalPattern = "9[78][01][0-9]{2}",
            postalExample = "97100",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BM",
        value = CountryValidationRule(
            countryCode = "BM",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:441(?:[2378][0-9]|5[0-39]|9[02])[0-9]{5})",
            phoneLengths = listOf(10),
            phoneExample = "4413701234",
            postalPattern = "[A-Z]{2} ?[A-Z0-9]{2}",
            postalExample = "FL 07",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BN",
        value = CountryValidationRule(
            countryCode = "BN",
            dialCode = "673",
            nationalPrefix = null,
            phonePattern = "(?:(?:22[89]|[78][0-9][0-9])[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "7123456",
            postalPattern = "[A-Z]{2} ?[0-9]{4}",
            postalExample = "BT2328",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BO",
        value = CountryValidationRule(
            countryCode = "BO",
            dialCode = "591",
            nationalPrefix = "0",
            phonePattern = "(?:(?:57|[67][0-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "71234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "BQ",
        value = CountryValidationRule(
            countryCode = "BQ",
            dialCode = "599",
            nationalPrefix = null,
            phonePattern = "(?:(?:31(?:8[14-8]|9[14578])|416[14-9]|7(?:0[01]|7[07]|8[0-9]|9[056])[0-9])[0-9]{3})",
            phoneLengths = listOf(7),
            phoneExample = "3181234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "BR",
        value = CountryValidationRule(
            countryCode = "BR",
            dialCode = "55",
            nationalPrefix = "0",
            phonePattern = "(?:(?:[14689][1-9]|2[12478]|3[1-578]|5[13-5]|7[13-579])(?:7|9[0-9])[0-9]{7})",
            phoneLengths = listOf(10, 11),
            phoneExample = "11961234567",
            postalPattern = "[0-9]{5}-?[0-9]{3}",
            postalExample = "40301-110",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BS",
        value = CountryValidationRule(
            countryCode = "BS",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:242(?:3(?:5[79]|7[56]|95)|4(?:[23][1-9]|4[1-35-9]|5[1-8]|6[2-8]|7[0-9]|81)|5(?:2[45]|3[35]|44|5[1-46-9]|65|77)|6[34]6|7(?:27|38)|8(?:0[1-9]|1[02-9]|2[0-9]|3[0-4]|[89]9))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "2423591234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "BT",
        value = CountryValidationRule(
            countryCode = "BT",
            dialCode = "975",
            nationalPrefix = null,
            phonePattern = "(?:(?:1[67]|[78]7)[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "17123456",
            postalPattern = "[0-9]{5}",
            postalExample = "11001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BW",
        value = CountryValidationRule(
            countryCode = "BW",
            dialCode = "267",
            nationalPrefix = null,
            phonePattern = "(?:(?:321|7(?:[1-8][0-9]|9[03]))[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "71123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "BY",
        value = CountryValidationRule(
            countryCode = "BY",
            dialCode = "375",
            nationalPrefix = "8",
            phonePattern = "(?:(?:2(?:5[5-79]|9[1-9])|(?:33|44)[0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "294911911",
            postalPattern = "[0-9]{6}",
            postalExample = "223016",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "BZ",
        value = CountryValidationRule(
            countryCode = "BZ",
            dialCode = "501",
            nationalPrefix = null,
            phonePattern = "(?:6[0-35-7][0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "6221234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CA",
        value = CountryValidationRule(
            countryCode = "CA",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:(?:2(?:04|[23]6|[48]9|5[07]|63)|3(?:06|43|54|6[578]|82)|4(?:03|1[68]|[26]8|3[178]|50|74)|5(?:06|1[49]|48|79|8[147])|6(?:04|[18]3|39|47|72)|7(?:0[59]|42|53|78|8[02])|8(?:[06]7|19|25|7[39])|9(?:0[25]|42))[2-9][0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "5062345678",
            postalPattern = "[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJ-NPRSTV-Z] ?[0-9][ABCEGHJ-NPRSTV-Z][0-9]",
            postalExample = "H3Z 2Y7",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CC",
        value = CountryValidationRule(
            countryCode = "CC",
            dialCode = "61",
            nationalPrefix = "0",
            phonePattern = "(?:4(?:79[01]|83[0-36-9]|95[0-3])[0-9]{5}|4(?:[0-36][0-9]|4[047-9]|[58][0-24-9]|7[02-8]|9[0-47-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "412345678",
            postalPattern = "6799",
            postalExample = "6799",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CD",
        value = CountryValidationRule(
            countryCode = "CD",
            dialCode = "243",
            nationalPrefix = "0",
            phonePattern = "(?:88[0-9]{5}|(?:8[0-69]|9[016-9])[0-9]{7})",
            phoneLengths = listOf(7, 9),
            phoneExample = "991234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CF",
        value = CountryValidationRule(
            countryCode = "CF",
            dialCode = "236",
            nationalPrefix = null,
            phonePattern = "(?:7[02-7][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "70012345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CG",
        value = CountryValidationRule(
            countryCode = "CG",
            dialCode = "242",
            nationalPrefix = null,
            phonePattern = "(?:026(?:1[0-5]|6[6-9])[0-9]{4}|0(?:[14-6][0-9][0-9]|2(?:40|5[5-8]|6[07-9]))[0-9]{5})",
            phoneLengths = listOf(9),
            phoneExample = "061234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CH",
        value = CountryValidationRule(
            countryCode = "CH",
            dialCode = "41",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6[89]|7[235-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "781234567",
            postalPattern = "[0-9]{4}",
            postalExample = "2544",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CI",
        value = CountryValidationRule(
            countryCode = "CI",
            dialCode = "225",
            nationalPrefix = null,
            phonePattern = "(?:0[157][0-9]{8})",
            phoneLengths = listOf(10),
            phoneExample = "0123456789",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CK",
        value = CountryValidationRule(
            countryCode = "CK",
            dialCode = "682",
            nationalPrefix = null,
            phonePattern = "(?:[578][0-9]{4})",
            phoneLengths = listOf(5),
            phoneExample = "71234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CL",
        value = CountryValidationRule(
            countryCode = "CL",
            dialCode = "56",
            nationalPrefix = null,
            phonePattern = "(?:2(?:1982[0-6]|3314[05-9])[0-9]{3}|(?:2(?:1(?:160|962)|3(?:(?:[24][0-9]|50)[0-9]|3(?:[0346-9][0-9]|1[0-35-9]|2[1-9]|5[0-24-9])|600)|646[59])|80[1-8][0-9][0-9]|9(?:(?:10[0-2]|7[1-9][0-9])[0-9]|3(?:[0-57-9][0-9][0-9]|6(?:0[02-9]|[1-9][0-9]))|6(?:[0-8][0-9][0-9]|9(?:[02-79][0-9]|1[05-9]))|9(?:[03-9][0-9][0-9]|1(?:[0235-9][0-9]|4[0-24-9])|2(?:[0-79][0-9]|8[0-46-9]))))[0-9]{4}|(?:22|3[2-5]|[47][1-35]|5[1-3578]|6[13-57]|8[1-9]|9[2458])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "221234567",
            postalPattern = "[0-9]{7}",
            postalExample = "8340457",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CM",
        value = CountryValidationRule(
            countryCode = "CM",
            dialCode = "237",
            nationalPrefix = null,
            phonePattern = "(?:(?:24[23]|6(?:[25-9][0-9]|4[0-2]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "671234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CN",
        value = CountryValidationRule(
            countryCode = "CN",
            dialCode = "86",
            nationalPrefix = "0",
            phonePattern = "(?:1(?:610[0-9]|740[0-5])[0-9]{6}|1(?:[38][0-9]|4[57]|[59][0-35-9]|6[25-7]|7[0-35-8])[0-9]{8})",
            phoneLengths = listOf(11),
            phoneExample = "13123456789",
            postalPattern = "[0-9]{6}",
            postalExample = "266033",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CO",
        value = CountryValidationRule(
            countryCode = "CO",
            dialCode = "57",
            nationalPrefix = "0",
            phonePattern = "(?:333301[0-5][0-9]{3}|3333(?:00|2[5-9]|[3-9][0-9])[0-9]{4}|(?:3(?:(?:0[0-5]|1[0-9]|5[01]|70)[0-9]|2(?:[0-3][0-9]|4[1-9])|3(?:00|3[0-24-9]))|9(?:101|408))[0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "3211234567",
            postalPattern = "[0-9]{6}",
            postalExample = "111221",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CR",
        value = CountryValidationRule(
            countryCode = "CR",
            dialCode = "506",
            nationalPrefix = null,
            phonePattern = "(?:(?:3005[0-9]|6500[01])[0-9]{3}|(?:5[07]|6[0-4]|7[0-3]|8[3-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "83123456",
            postalPattern = "[0-9]{4,5}|[0-9]{3}-[0-9]{4}",
            postalExample = "1000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CU",
        value = CountryValidationRule(
            countryCode = "CU",
            dialCode = "53",
            nationalPrefix = "0",
            phonePattern = "(?:(?:5[0-9]|6[2-4])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "51234567",
            postalPattern = "[0-9]{5}",
            postalExample = "10700",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules2() {
    put(
        key = "CV",
        value = CountryValidationRule(
            countryCode = "CV",
            dialCode = "238",
            nationalPrefix = null,
            phonePattern = "(?:(?:36|5[1-389]|9[0-9])[0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "9911234",
            postalPattern = "[0-9]{4}",
            postalExample = "7600",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CW",
        value = CountryValidationRule(
            countryCode = "CW",
            dialCode = "599",
            nationalPrefix = null,
            phonePattern = "(?:953[01][0-9]{4}|9(?:5[12467]|6[5-9])[0-9]{5})",
            phoneLengths = listOf(7, 8),
            phoneExample = "95181234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "CX",
        value = CountryValidationRule(
            countryCode = "CX",
            dialCode = "61",
            nationalPrefix = "0",
            phonePattern = "(?:4(?:79[01]|83[0-36-9]|95[0-3])[0-9]{5}|4(?:[0-36][0-9]|4[047-9]|[58][0-24-9]|7[02-8]|9[0-47-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "412345678",
            postalPattern = "6798",
            postalExample = "6798",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CY",
        value = CountryValidationRule(
            countryCode = "CY",
            dialCode = "357",
            nationalPrefix = null,
            phonePattern = "(?:9(?:10|[4-79][0-9])[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "96123456",
            postalPattern = "[0-9]{4}",
            postalExample = "2008",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "CZ",
        value = CountryValidationRule(
            countryCode = "CZ",
            dialCode = "420",
            nationalPrefix = null,
            phonePattern = "(?:7060[0-9]{5}|(?:60[1-8]|7(?:0[2-5]|19|[2379][0-9]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "601123456",
            postalPattern = "[0-9]{3} ?[0-9]{2}",
            postalExample = "100 00",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "DE",
        value = CountryValidationRule(
            countryCode = "DE",
            dialCode = "49",
            nationalPrefix = "0",
            phonePattern = "(?:1(?:6[023]|7[0-9])[0-9]{7,8}|15(?:[0-25-9][0-9][0-9]|3(?:10|33))[0-9]{6})",
            phoneLengths = listOf(10, 11),
            phoneExample = "15123456789",
            postalPattern = "[0-9]{5}",
            postalExample = "26133",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "DJ",
        value = CountryValidationRule(
            countryCode = "DJ",
            dialCode = "253",
            nationalPrefix = null,
            phonePattern = "(?:77[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "77831001",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "DK",
        value = CountryValidationRule(
            countryCode = "DK",
            dialCode = "45",
            nationalPrefix = null,
            phonePattern = "(?:(?:2[6-8]|37|6[78]|96)[0-9]{6}|(?:2[0-59]|3[0-689]|[457][0-9]|6[0-69]|8[126-9]|9[1-47-9])[1-9][0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "34412345",
            postalPattern = "[0-9]{4}",
            postalExample = "8660",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "DM",
        value = CountryValidationRule(
            countryCode = "DM",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:767(?:2(?:[2-4689]5|7[5-7])|31[5-7]|61[1-8]|70[1-6])[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "7672251234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "DO",
        value = CountryValidationRule(
            countryCode = "DO",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:8[024]9[2-9][0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "8092345678",
            postalPattern = "[0-9]{5}",
            postalExample = "11903",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "DZ",
        value = CountryValidationRule(
            countryCode = "DZ",
            dialCode = "213",
            nationalPrefix = "0",
            phonePattern = "(?:5(?:4[0-29]|6[0-4])[0-9]{6}|(?:55|6[0-9]|7[7-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "551234567",
            postalPattern = "[0-9]{5}",
            postalExample = "40304",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "EC",
        value = CountryValidationRule(
            countryCode = "EC",
            dialCode = "593",
            nationalPrefix = "0",
            phonePattern = "(?:964[0-2][0-9]{5}|9(?:39|[57][89]|6[0-36-9]|[89][0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "991234567",
            postalPattern = "[0-9]{6}",
            postalExample = "090105",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "EE",
        value = CountryValidationRule(
            countryCode = "EE",
            dialCode = "372",
            nationalPrefix = null,
            phonePattern = "(?:(?:5[0-9]{5}|8(?:1(?:0(?:0(?:00|[178][0-9])|[3-9][0-9][0-9])|(?:1(?:0[2-6]|1[0-9])|[2-79][0-9][0-9])[0-9])|2(?:0(?:0(?:00|4[0-9])|(?:19|[2-7][0-9])[0-9])|(?:(?:[124-69][0-9]|3[5-9])[0-9]|7(?:[0-79][0-9]|8[013-9])|8(?:[2-6][0-9]|7[01]))[0-9])|[349][0-9]{4}))[0-9][0-9]|5(?:(?:[02][0-9]|5[0-478])[0-9]|1(?:[0-8][0-9]|95)|6(?:4[0-4]|5[1-589]))[0-9]{3})",
            phoneLengths = listOf(7, 8),
            phoneExample = "51234567",
            postalPattern = "[0-9]{5}",
            postalExample = "69501",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "EG",
        value = CountryValidationRule(
            countryCode = "EG",
            dialCode = "20",
            nationalPrefix = "0",
            phonePattern = "(?:1[0-25][0-9]{8})",
            phoneLengths = listOf(10),
            phoneExample = "1001234567",
            postalPattern = "[0-9]{7}|[0-9]{5}",
            postalExample = "4460232",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "EH",
        value = CountryValidationRule(
            countryCode = "EH",
            dialCode = "212",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6(?:[0-79][0-9]|8[0-247-9])|7(?:[016-8][0-9]|2[0-8]|3[01]|5[0-5]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "650123456",
            postalPattern = "[0-9]{5}",
            postalExample = "70000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ER",
        value = CountryValidationRule(
            countryCode = "ER",
            dialCode = "291",
            nationalPrefix = "0",
            phonePattern = "(?:(?:17[1-3]|7[0-9][0-9])[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "7123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "ES",
        value = CountryValidationRule(
            countryCode = "ES",
            dialCode = "34",
            nationalPrefix = null,
            phonePattern = "(?:96906(?:09|10)[0-9][0-9]|(?:590(?:10[0-2]|600)|97390[0-9])[0-9]{3}|(?:6[0-9]|7[1-48])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "612345678",
            postalPattern = "[0-9]{5}",
            postalExample = "28039",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ET",
        value = CountryValidationRule(
            countryCode = "ET",
            dialCode = "251",
            nationalPrefix = "0",
            phonePattern = "(?:700[1-9][0-9]{5}|(?:7(?:0[1-9]|1[0-8]|2[1-35-79]|3[0-9]|77|86|99)|(?:8[01]|9[0-9])[0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "911234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "FI",
        value = CountryValidationRule(
            countryCode = "FI",
            dialCode = "358",
            nationalPrefix = "0",
            phonePattern = "(?:4946[0-9]{2,6}|(?:4[0-8]|50)[0-9]{4,8})",
            phoneLengths = listOf(6, 7, 8, 9, 10),
            phoneExample = "412345678",
            postalPattern = "[0-9]{5}",
            postalExample = "00550",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "FJ",
        value = CountryValidationRule(
            countryCode = "FJ",
            dialCode = "679",
            nationalPrefix = null,
            phonePattern = "(?:(?:[279][0-9]|45|5[01568]|8[034679])[0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "7012345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "FK",
        value = CountryValidationRule(
            countryCode = "FK",
            dialCode = "500",
            nationalPrefix = null,
            phonePattern = "(?:[56][0-9]{4})",
            phoneLengths = listOf(5),
            phoneExample = "51234",
            postalPattern = "FIQQ 1ZZ",
            postalExample = "FIQQ 1ZZ",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "FM",
        value = CountryValidationRule(
            countryCode = "FM",
            dialCode = "691",
            nationalPrefix = null,
            phonePattern = "(?:31(?:00[67]|208|309)[0-9][0-9]|(?:3(?:[2357]0[1-9]|602|804|905)|(?:820|9[2-7][0-9])[0-9])[0-9]{3})",
            phoneLengths = listOf(7),
            phoneExample = "3501234",
            postalPattern = "(9694[1-4])(?:[ \\-]([0-9]{4}))?",
            postalExample = "96941",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "FO",
        value = CountryValidationRule(
            countryCode = "FO",
            dialCode = "298",
            nationalPrefix = null,
            phonePattern = "(?:(?:[27][1-9]|5[0-9]|9[16])[0-9]{4})",
            phoneLengths = listOf(6),
            phoneExample = "211234",
            postalPattern = "[0-9]{3}",
            postalExample = "100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "FR",
        value = CountryValidationRule(
            countryCode = "FR",
            dialCode = "33",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6(?:[0-24-8][0-9]|3[0-8]|9[589])|7[3-9][0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "612345678",
            postalPattern = "[0-9]{2} ?[0-9]{3}",
            postalExample = "33380",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GA",
        value = CountryValidationRule(
            countryCode = "GA",
            dialCode = "241",
            nationalPrefix = null,
            phonePattern = "(?:(?:(?:0[2-7]|7[467])[0-9]|6(?:0[0-4]|10|[256][0-9]))[0-9]{5}|[2-7][0-9]{6})",
            phoneLengths = listOf(7, 8),
            phoneExample = "06031234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules3() {
    put(
        key = "GB",
        value = CountryValidationRule(
            countryCode = "GB",
            dialCode = "44",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:457[0-57-9]|700[01]|911[028])[0-9]{5}|7(?:[1-3][0-9][0-9]|4(?:[0-46-9][0-9]|5[0-689])|5(?:0[0-8]|[13-9][0-9]|2[0-35-9])|7(?:0[1-9]|[1-7][0-9]|8[02-9]|9[0-689])|8(?:[014-9][0-9]|[23][0-8])|9(?:[024-9][0-9]|1[02-9]|3[0-689]))[0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "7400123456",
            postalPattern = "GIR ?0AA|(?:(?:AB|AL|B|BA|BB|BD|BF|BH|BL|BN|BR|BS|BT|BX|CA|CB|CF|CH|CM|CO|CR|CT|CV|CW|DA|DD|DE|DG|DH|DL|DN|DT|DY|E|EC|EH|EN|EX|FK|FY|G|GL|GY|GU|HA|HD|HG|HP|HR|HS|HU|HX|IG|IM|IP|IV|JE|KA|KT|KW|KY|L|LA|LD|LE|LL|LN|LS|LU|M|ME|MK|ML|N|NE|NG|NN|NP|NR|NW|OL|OX|PA|PE|PH|PL|PO|PR|RG|RH|RM|S|SA|SE|SG|SK|SL|SM|SN|SO|SP|SR|SS|ST|SW|SY|TA|TD|TF|TN|TQ|TR|TS|TW|UB|W|WA|WC|WD|WF|WN|WR|WS|WV|YO|ZE)(?:[0-9][0-9A-Z]? ?[0-9][ABD-HJLN-UW-Z]{2}))|BFPO ?[0-9]{1,4}",
            postalExample = "EC1Y 8SY",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GD",
        value = CountryValidationRule(
            countryCode = "GD",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:473(?:4(?:0[2-79]|1[04-9]|2[0-5]|49|5[6-8])|5(?:2[01]|3[3-8])|901)[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "4734031234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "GE",
        value = CountryValidationRule(
            countryCode = "GE",
            dialCode = "995",
            nationalPrefix = "0",
            phonePattern = "(?:5(?:(?:(?:0555|1(?:[17]77|555))[5-9]|757(?:7[7-9]|8[01]))[0-9]|22252[0-4])[0-9][0-9]|5(?:0(?:0(?:1[09]|70)|505)|1(?:0[01]0|1(?:07|33|51))|2(?:0[02]0|2[25]2)|3(?:0[03]0|3[35]3)|4(?:0[04]0|411)|5222|9000)[0-4][0-9]{3}|(?:5(?:0(?:0(?:0[0-9]|1[12]|2[02]|3[0-6]|4[04]|5[05]|77|88|9[09])|(?:[14][0-9]|77)[0-9]|22[02])|1(?:1(?:[03][01]|[124][0-9]|5[02-6]|7[0-6])|4[0-9][0-9])|2(?:228|555)|3555|4(?:4(?:[02-9][0-9]|14)|555)|5(?:[0157-9][0-9][0-9]|200|333|4(?:44|55))|6[89][0-9][0-9]|7(?:(?:[0147-9][0-9]|22)[0-9]|5(?:00|[57]5))|8(?:0(?:[018][0-9]|2[0-4])|5(?:55|8[89])|8(?:55|88))|9(?:090|[1-35-9][0-9][0-9]))|790[0-9][0-9])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "555123456",
            postalPattern = "[0-9]{4}",
            postalExample = "0101",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GF",
        value = CountryValidationRule(
            countryCode = "GF",
            dialCode = "594",
            nationalPrefix = "0",
            phonePattern = "(?:(?:694(?:[0-249][0-9]|3[0-8])|7093[0-3])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "694201234",
            postalPattern = "9[78]3[0-9]{2}",
            postalExample = "97300",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GG",
        value = CountryValidationRule(
            countryCode = "GG",
            dialCode = "44",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:(?:781|839)[0-9]|911[17])[0-9]{5})",
            phoneLengths = listOf(10),
            phoneExample = "7781123456",
            postalPattern = "GY[0-9][0-9A-Z]? ?[0-9][ABD-HJLN-UW-Z]{2}",
            postalExample = "GY1 1AA",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GH",
        value = CountryValidationRule(
            countryCode = "GH",
            dialCode = "233",
            nationalPrefix = "0",
            phonePattern = "(?:(?:2(?:[0346-9][0-9]|5[67])|5(?:[03-7][0-9]|9[1-9]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "231234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "GI",
        value = CountryValidationRule(
            countryCode = "GI",
            dialCode = "350",
            nationalPrefix = null,
            phonePattern = "(?:5251[0-4][0-9]{3}|(?:5(?:[146-8][0-9][0-9]|250)|60(?:1[01]|6[0-9]))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "57123456",
            postalPattern = "GX11 1AA",
            postalExample = "GX11 1AA",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GL",
        value = CountryValidationRule(
            countryCode = "GL",
            dialCode = "299",
            nationalPrefix = null,
            phonePattern = "(?:[245][0-9]{5})",
            phoneLengths = listOf(6),
            phoneExample = "221234",
            postalPattern = "39[0-9]{2}",
            postalExample = "3900",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GM",
        value = CountryValidationRule(
            countryCode = "GM",
            dialCode = "220",
            nationalPrefix = null,
            phonePattern = "(?:(?:(?:[23679][0-9]|4[015]|8(?:(?:3[35]|6[68]|99)[0-9]|7(?:[27][0-9]|4[015])))[0-9]|5(?:[0-489][0-9]|56))[0-9]{4}|8[4-7][0-9]{5})",
            phoneLengths = listOf(7, 9),
            phoneExample = "3012345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "GN",
        value = CountryValidationRule(
            countryCode = "GN",
            dialCode = "224",
            nationalPrefix = null,
            phonePattern = "(?:6[0-356][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "601123456",
            postalPattern = "[0-9]{3}",
            postalExample = "001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GP",
        value = CountryValidationRule(
            countryCode = "GP",
            dialCode = "590",
            nationalPrefix = "0",
            phonePattern = "(?:(?:69(?:0[0-9][0-9]|1(?:2[2-9]|3[0-5]))|7090[0-4])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "690001234",
            postalPattern = "9[78][01][0-9]{2}",
            postalExample = "97100",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GQ",
        value = CountryValidationRule(
            countryCode = "GQ",
            dialCode = "240",
            nationalPrefix = null,
            phonePattern = "(?:(?:222|55[0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "222123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "GR",
        value = CountryValidationRule(
            countryCode = "GR",
            dialCode = "30",
            nationalPrefix = null,
            phonePattern = "(?:68[57-9][0-9]{7}|(?:69|94)[0-9]{8})",
            phoneLengths = listOf(10),
            phoneExample = "6912345678",
            postalPattern = "[0-9]{3} ?[0-9]{2}",
            postalExample = "151 24",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GT",
        value = CountryValidationRule(
            countryCode = "GT",
            dialCode = "502",
            nationalPrefix = null,
            phonePattern = "(?:(?:[3-5][0-9][0-9]|80[0-4])[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "51234567",
            postalPattern = "[0-9]{5}",
            postalExample = "09001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GU",
        value = CountryValidationRule(
            countryCode = "GU",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:671(?:2[0-9][0-9]|3(?:00|3[39]|4[349]|55|6[26])|4(?:00|56|7[1-9]|8[02-9])|5(?:55|6[2-5]|88)|6(?:3[2-578]|4[24-9]|5[34]|78|8[235-9])|7(?:[0479]7|2[0167]|3[45]|8[7-9])|8(?:[2-57-9]8|6[478])|9(?:2[29]|6[79]|7[1279]|8[7-9]|9[16-9]))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "6713001234",
            postalPattern = "(969(?:[12][0-9]|3[12]))(?:[ \\-]([0-9]{4}))?",
            postalExample = "96910",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GW",
        value = CountryValidationRule(
            countryCode = "GW",
            dialCode = "245",
            nationalPrefix = null,
            phonePattern = "(?:9(?:5[0-9]|6[569]|77)[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "955012345",
            postalPattern = "[0-9]{4}",
            postalExample = "1000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "GY",
        value = CountryValidationRule(
            countryCode = "GY",
            dialCode = "592",
            nationalPrefix = null,
            phonePattern = "(?:(?:51[01]|6[0-9][0-9]|7(?:[0-5][0-9]|6[0-79]|70|8[067]))[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "6091234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "HK",
        value = CountryValidationRule(
            countryCode = "HK",
            dialCode = "852",
            nationalPrefix = null,
            phonePattern = "(?:(?:4(?:0(?:6[0-3]|9[3-6])|24[3-6]|44[0-35-9]|6(?:4[0-57-9]|6[0-6])|7(?:4[0-48]|6[0-5]))|5(?:25[3-7]|35[4-8]|73[0-6]|95[0-8])|6(?:26[013-8]|(?:66|78)[0-5])|7(?:0(?:7[1-8]|8[0-8])|10[1-4])|84(?:4[0-2]|8[0-35-9])|9(?:29[013-9]|39[014-9]|59[0-467]|899))[0-9]{4}|(?:4(?:4[0-35-9]|6[0-357-9]|7[0-35])|5(?:[1-59][0-46-9]|6[0-4689]|7[0-246-9])|6(?:0[1-9]|[13-59][0-9]|[268][0-57-9]|7[0-79])|70[1-59]|84[0-39]|9(?:0[1-9]|1[02-9]|[2358][0-8]|[467][0-9]))[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "51234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "HN",
        value = CountryValidationRule(
            countryCode = "HN",
            dialCode = "504",
            nationalPrefix = null,
            phonePattern = "(?:[37-9][0-9]{7})",
            phoneLengths = listOf(8),
            phoneExample = "91234567",
            postalPattern = "[0-9]{5}",
            postalExample = "31301",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "HR",
        value = CountryValidationRule(
            countryCode = "HR",
            dialCode = "385",
            nationalPrefix = "0",
            phonePattern = "(?:9(?:(?:0[1-9]|[12589][0-9])[0-9][0-9]|7(?:[0679][0-9][0-9]|5(?:[01][0-9]|44|55|77|9[5-79])))[0-9]{4}|98[0-9]{6})",
            phoneLengths = listOf(8, 9),
            phoneExample = "921234567",
            postalPattern = "[0-9]{5}",
            postalExample = "10000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "HT",
        value = CountryValidationRule(
            countryCode = "HT",
            dialCode = "509",
            nationalPrefix = null,
            phonePattern = "(?:(?:[34][0-9]|5[568])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "34101234",
            postalPattern = "[0-9]{4}",
            postalExample = "6120",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "HU",
        value = CountryValidationRule(
            countryCode = "HU",
            dialCode = "36",
            nationalPrefix = "06",
            phonePattern = "(?:(?:[257]0|3[01])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "201234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1037",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ID",
        value = CountryValidationRule(
            countryCode = "ID",
            dialCode = "62",
            nationalPrefix = "0",
            phonePattern = "(?:8[1-35-9][0-9]{7,10})",
            phoneLengths = listOf(9, 10, 11, 12),
            phoneExample = "812345678",
            postalPattern = "[0-9]{5}",
            postalExample = "40115",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IE",
        value = CountryValidationRule(
            countryCode = "IE",
            dialCode = "353",
            nationalPrefix = "0",
            phonePattern = "(?:8(?:22|[35-9][0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "850123456",
            postalPattern = "[0-9A-Z]{3} ?[0-9A-Z]{4}",
            postalExample = "A65 F4E2",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IL",
        value = CountryValidationRule(
            countryCode = "IL",
            dialCode = "972",
            nationalPrefix = "0",
            phonePattern = "(?:55(?:4(?:0[0-3]|10|6[015-9])|57[0-289]|8[06][0-2])[0-9]{4}|5(?:(?:[0-2][02-9]|[36][0-9]|[49][2-9]|8[3-7])[0-9]|5(?:01|2[0-9]|3[0-3]|4[3-5]|5[0-25689]|6[6-8]|7[0-267]|8[7-9]|9[1-9]))[0-9]{5})",
            phoneLengths = listOf(9),
            phoneExample = "502345678",
            postalPattern = "[0-9]{5}(?:[0-9]{2})?",
            postalExample = "9614303",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules4() {
    put(
        key = "IM",
        value = CountryValidationRule(
            countryCode = "IM",
            dialCode = "44",
            nationalPrefix = "0",
            phonePattern = "(?:76245[06][0-9]{4}|7(?:4576|[59]24[0-9]|624[0-4689])[0-9]{5})",
            phoneLengths = listOf(10),
            phoneExample = "7924123456",
            postalPattern = "IM[0-9][0-9A-Z]? ?[0-9][ABD-HJLN-UW-Z]{2}",
            postalExample = "IM2 1AA",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IN",
        value = CountryValidationRule(
            countryCode = "IN",
            dialCode = "91",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6(?:1279|828[01489])|7(?:887[02-9]|9313)|8(?:079[04-9]|(?:84|91)7[02-8]))[0-9]{5}|(?:160[01]|6(?:(?:12|[2-4]1|5[17]|6[13]|80)[0189]|7(?:1[0189]|86))|7(?:1(?:2[0189]|9[0-5])|3(?:2[5-8]|[34][017-9]|9[016-9])|5(?:[15][017-9]|2[04-9]|9[7-9])|6(?:0[0-47]|1[0-257-9]|2[0-4]|3[19]|5[4589])|70[0289]|88[089])|8(?:0(?:6[67]|7[02-8])|70[017-9]|84[01489]|91[0-289]))[0-9]{6}|(?:731|8(?:16|2[014]|3[126]|6[136]|7[78]|83))(?:[0189][0-9]|7[02-8])[0-9]{5}|(?:6(?:(?:1[1358]|2[2457]|3[2-4]|4[235-7]|5[2-689]|6[24578])[0-9]|7(?:[23569][0-9]|4[0189]|8[0-57-9])|8(?:[14-6][0-9]|2[0-79]))|7(?:1(?:[013-8][0-9]|9[6-9])|3(?:2[0-49]|9[2-5])|5(?:2[1-3]|9[0-6])|6(?:0[5689]|2[5-9]|3[02-8]|4[0-9]|5[0-367])|70[13-7]|881))[0189][0-9]{5}|(?:6(?:[09][0-9]|1[04679]|2[03689]|3[05-9]|4[0489]|50|6[069]|7[07]|8[7-9])|7(?:[024][0-9]|3[05-8]|5[0346-8]|6[6-9]|7[1-9]|8[0-79]|9[07-9])|8(?:0[01589]|1[0-57-9]|2[235-9]|3[03-57-9]|[45][0-9]|6[02457-9]|7[1-69]|8[0-25-9]|9[02-9])|9[0-9][0-9])[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "8123456789",
            postalPattern = "[0-9]{6}",
            postalExample = "110034",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IO",
        value = CountryValidationRule(
            countryCode = "IO",
            dialCode = "246",
            nationalPrefix = null,
            phonePattern = "(?:38[0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "3801234",
            postalPattern = "BBND 1ZZ",
            postalExample = "BBND 1ZZ",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IQ",
        value = CountryValidationRule(
            countryCode = "IQ",
            dialCode = "964",
            nationalPrefix = "0",
            phonePattern = "(?:7[3-9][0-9]{8})",
            phoneLengths = listOf(10),
            phoneExample = "7912345678",
            postalPattern = "[0-9]{5}",
            postalExample = "31001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IR",
        value = CountryValidationRule(
            countryCode = "IR",
            dialCode = "98",
            nationalPrefix = "0",
            phonePattern = "(?:9(?:(?:0[0-5]|[13][0-9]|2[0-3])[0-9][0-9]|9(?:[0-46][0-9][0-9]|5(?:10|5[0-9])|8(?:[12][0-9]|3[0-2]|88)|9(?:[01359][0-9]|21|69|77|8[7-9])))[0-9]{5})",
            phoneLengths = listOf(10),
            phoneExample = "9123456789",
            postalPattern = "[0-9]{5}-?[0-9]{5}",
            postalExample = "11936-12345",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IS",
        value = CountryValidationRule(
            countryCode = "IS",
            dialCode = "354",
            nationalPrefix = null,
            phonePattern = "(?:(?:38[589][0-9][0-9]|6(?:1[1-8]|2[0-6]|3[026-9]|4[014679]|5[0159]|6[0-69]|70|8[06-8]|9[0-9])|7(?:5[057]|[6-9][0-9])|8(?:2[0-59]|[3-69][0-9]|8[238]))[0-9]{4})",
            phoneLengths = listOf(7, 9),
            phoneExample = "6111234",
            postalPattern = "[0-9]{3}",
            postalExample = "320",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "IT",
        value = CountryValidationRule(
            countryCode = "IT",
            dialCode = "39",
            nationalPrefix = null,
            phonePattern = "(?:3[2-9][0-9]{7,8}|(?:31|43)[0-9]{8})",
            phoneLengths = listOf(9, 10),
            phoneExample = "3123456789",
            postalPattern = "[0-9]{5}",
            postalExample = "00144",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "JE",
        value = CountryValidationRule(
            countryCode = "JE",
            dialCode = "44",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:(?:(?:50|82)9|937)[0-9]|7(?:00[378]|97[0-9]))[0-9]{5})",
            phoneLengths = listOf(10),
            phoneExample = "7797712345",
            postalPattern = "JE[0-9][0-9A-Z]? ?[0-9][ABD-HJLN-UW-Z]{2}",
            postalExample = "JE1 1AA",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "JM",
        value = CountryValidationRule(
            countryCode = "JM",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:(?:6582(?:[0-4][0-9]|95)|876(?:2(?:0[1-9]|[13-9][0-9]|2[013-9])|[348][0-9][0-9]|5(?:0[1-9]|[1-9][0-9])|6(?:4[89]|6[67])|7(?:0[07]|7[0-9]|8[1-47-9]|9[0-36-9])|9(?:[01]9|9[0579])))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "8762101234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "JO",
        value = CountryValidationRule(
            countryCode = "JO",
            dialCode = "962",
            nationalPrefix = "0",
            phonePattern = "(?:(?:427|7(?:11|[29][0-9]|[78][0-25-9]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "790123456",
            postalPattern = "[0-9]{5}",
            postalExample = "11937",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "JP",
        value = CountryValidationRule(
            countryCode = "JP",
            dialCode = "81",
            nationalPrefix = "0",
            phonePattern = "(?:[6-9]0[1-9][0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "9012345678",
            postalPattern = "[0-9]{3}-?[0-9]{4}",
            postalExample = "154-0023",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KE",
        value = CountryValidationRule(
            countryCode = "KE",
            dialCode = "254",
            nationalPrefix = "0",
            phonePattern = "(?:(?:1(?:0[0-8]|[18][0-9]|2[014]|30|4[0-5])|7[0-9][0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "712123456",
            postalPattern = "[0-9]{5}",
            postalExample = "20100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KG",
        value = CountryValidationRule(
            countryCode = "KG",
            dialCode = "996",
            nationalPrefix = "0",
            phonePattern = "(?:312(?:58[0-9]|973)[0-9]{3}|(?:2(?:0[0-35]|2[0-9])|5[0-24-7][0-9]|600|7(?:[07][0-9]|55)|88[08]|9(?:12|9[05-9]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "700123456",
            postalPattern = "[0-9]{6}",
            postalExample = "720001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KH",
        value = CountryValidationRule(
            countryCode = "KH",
            dialCode = "855",
            nationalPrefix = "0",
            phonePattern = "(?:(?:(?:1[28]|3[18]|9[67])[0-9]|6[016-9]|7(?:[07-9]|[16][0-9])|8(?:[013-79]|8[0-9]))[0-9]{6}|(?:1[0-9]|9[0-57-9])[0-9]{6}|(?:2[3-6]|3[2-6]|4[2-4]|[5-7][2-5])48[0-9]{5})",
            phoneLengths = listOf(8, 9),
            phoneExample = "91234567",
            postalPattern = "[0-9]{5,6}",
            postalExample = "120101",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KI",
        value = CountryValidationRule(
            countryCode = "KI",
            dialCode = "686",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6200[01]|7(?:310[1-9]|5(?:02[03-9]|12[0-47-9]|22[0-7]|[34](?:0[1-9]|8[02-9])|50[1-9])))[0-9]{3}|(?:63[0-9][0-9]|7(?:(?:[0146-9][0-9]|2[0-689])[0-9]|3(?:[02-9][0-9]|1[1-9])|5(?:[0-2][013-9]|[34][1-79]|5[1-9]|[6-9][0-9])))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "72001234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "KM",
        value = CountryValidationRule(
            countryCode = "KM",
            dialCode = "269",
            nationalPrefix = null,
            phonePattern = "(?:[34][0-9]{6})",
            phoneLengths = listOf(7),
            phoneExample = "3212345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "KN",
        value = CountryValidationRule(
            countryCode = "KN",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:869(?:48[89]|55[6-8]|66[0-9]|76[02-7])[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "8697652917",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "KP",
        value = CountryValidationRule(
            countryCode = "KP",
            dialCode = "850",
            nationalPrefix = "0",
            phonePattern = "(?:19[1-3][0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "1921234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = null,
        ),
    )
    put(
        key = "KR",
        value = CountryValidationRule(
            countryCode = "KR",
            dialCode = "82",
            nationalPrefix = "0",
            phonePattern = "(?:1(?:05(?:[0-8][0-9]|9[0-6])|22[13][0-9])[0-9]{4,5}|1(?:0[0-46-9]|[16-9][0-9]|2[013-9])[0-9]{6,7})",
            phoneLengths = listOf(9, 10),
            phoneExample = "1020000000",
            postalPattern = "[0-9]{5}",
            postalExample = "03051",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KW",
        value = CountryValidationRule(
            countryCode = "KW",
            dialCode = "965",
            nationalPrefix = null,
            phonePattern = "(?:(?:41[0-9][0-9]|5(?:(?:[05][0-9]|1[0-7]|6[56])[0-9]|2(?:22|5[25])|7(?:55|77)|88[58])|6(?:(?:0[034679]|5[015-9]|6[0-9])[0-9]|1(?:00|11|6[16])|2[26]2|3[36]3|4[46]4|7(?:0[013-9]|[67][0-9])|8[68]8|9(?:[069][0-9]|3[039]))|9(?:(?:[04679][0-9]|8[057-9])[0-9]|1(?:00|1[01]|99)|2(?:00|2[0-9])|3(?:00|3[03])|5(?:00|5[0-9])))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "50012345",
            postalPattern = "[0-9]{5}",
            postalExample = "54541",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KY",
        value = CountryValidationRule(
            countryCode = "KY",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:345(?:32[1-9]|4(?:1[2-6]|2[0-4])|5(?:1[67]|2[5-79]|4[6-9]|50|76)|649|82[56]|9(?:1[679]|2[2-9]|3[06-9]|90))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "3453231234",
            postalPattern = "KY[0-9]-[0-9]{4}",
            postalExample = "KY1-1100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "KZ",
        value = CountryValidationRule(
            countryCode = "KZ",
            dialCode = "7",
            nationalPrefix = "8",
            phonePattern = "(?:7(?:0[0-25-8]|47|6[0-4]|7[15-8]|85)[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "7710009998",
            postalPattern = "[0-9]{6}",
            postalExample = "040900",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LA",
        value = CountryValidationRule(
            countryCode = "LA",
            dialCode = "856",
            nationalPrefix = "0",
            phonePattern = "(?:(?:20(?:[23579][0-9]|8[78])|30[24][0-9])[0-9]{6}|30[0-9]{7})",
            phoneLengths = listOf(9, 10),
            phoneExample = "2023123456",
            postalPattern = "[0-9]{5}",
            postalExample = "01160",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LB",
        value = CountryValidationRule(
            countryCode = "LB",
            dialCode = "961",
            nationalPrefix = "0",
            phonePattern = "(?:(?:(?:3|81)[0-9]|7(?:[01][0-9]|6[013-9]|8[7-9]|9[0-4]))[0-9]{5})",
            phoneLengths = listOf(7, 8),
            phoneExample = "71123456",
            postalPattern = "(?:[0-9]{4})(?: ?(?:[0-9]{4}))?",
            postalExample = "2038 3054",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LC",
        value = CountryValidationRule(
            countryCode = "LC",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:758(?:28[4-7]|384|4(?:6[01]|8[4-9])|5(?:1[89]|20|84)|7(?:1[2-9]|2[0-9]|3[0-3])|812)[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "7582845678",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules5() {
    put(
        key = "LI",
        value = CountryValidationRule(
            countryCode = "LI",
            dialCode = "423",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6(?:(?:4[5-9]|5[0-9])[0-9]|6(?:[024-68][0-9]|1[01]|3[7-9]|7[02]))[0-9]|7(?:[37-9][0-9]|42|56))[0-9]{4})",
            phoneLengths = listOf(7, 9),
            phoneExample = "660234567",
            postalPattern = "948[5-9]|949[0-8]",
            postalExample = "9496",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LK",
        value = CountryValidationRule(
            countryCode = "LK",
            dialCode = "94",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:[0-25-8][0-9]|4[0-4])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "712345678",
            postalPattern = "[0-9]{5}",
            postalExample = "20000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LR",
        value = CountryValidationRule(
            countryCode = "LR",
            dialCode = "231",
            nationalPrefix = "0",
            phonePattern = "(?:(?:(?:(?:22|33)0|555|7(?:6[01]|7[0-9])|88[0-9])[0-9]|4(?:240|[67]))[0-9]{5}|[56][0-9]{6})",
            phoneLengths = listOf(7, 9),
            phoneExample = "770123456",
            postalPattern = "[0-9]{4}",
            postalExample = "1000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LS",
        value = CountryValidationRule(
            countryCode = "LS",
            dialCode = "266",
            nationalPrefix = null,
            phonePattern = "(?:[56][0-9]{7})",
            phoneLengths = listOf(8),
            phoneExample = "50123456",
            postalPattern = "[0-9]{3}",
            postalExample = "100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LT",
        value = CountryValidationRule(
            countryCode = "LT",
            dialCode = "370",
            nationalPrefix = "0",
            phonePattern = "(?:6[0-9]{7})",
            phoneLengths = listOf(8),
            phoneExample = "61234567",
            postalPattern = "[0-9]{5}",
            postalExample = "04340",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LU",
        value = CountryValidationRule(
            countryCode = "LU",
            dialCode = "352",
            nationalPrefix = null,
            phonePattern = "(?:6(?:[26][18]|5[1568]|7[189]|81|9[128])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "628123456",
            postalPattern = "[0-9]{4}",
            postalExample = "4750",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LV",
        value = CountryValidationRule(
            countryCode = "LV",
            dialCode = "371",
            nationalPrefix = null,
            phonePattern = "(?:2333[0-8][0-9]{3}|2(?:[0-24-9][0-9][0-9]|3(?:0[07]|[14-9][0-9]|2[02-9]|3[0-24-9]))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "21234567",
            postalPattern = "LV-[0-9]{4}",
            postalExample = "LV-1073",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "LY",
        value = CountryValidationRule(
            countryCode = "LY",
            dialCode = "218",
            nationalPrefix = "0",
            phonePattern = "(?:9[1-6][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "912345678",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "MA",
        value = CountryValidationRule(
            countryCode = "MA",
            dialCode = "212",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6(?:[0-79][0-9]|8[0-247-9])|7(?:[016-8][0-9]|2[0-8]|3[01]|5[0-5]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "650123456",
            postalPattern = "[0-9]{5}",
            postalExample = "53000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MC",
        value = CountryValidationRule(
            countryCode = "MC",
            dialCode = "377",
            nationalPrefix = "0",
            phonePattern = "(?:4(?:[46][0-9]|5[1-9])[0-9]{5}|(?:3|[67][0-9])[0-9]{7})",
            phoneLengths = listOf(8, 9),
            phoneExample = "612345678",
            postalPattern = "980[0-9]{2}",
            postalExample = "98000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MD",
        value = CountryValidationRule(
            countryCode = "MD",
            dialCode = "373",
            nationalPrefix = "0",
            phonePattern = "(?:562[0-9]{5}|(?:6[0-9]|7[16-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "62112345",
            postalPattern = "[0-9]{4}",
            postalExample = "2012",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ME",
        value = CountryValidationRule(
            countryCode = "ME",
            dialCode = "382",
            nationalPrefix = "0",
            phonePattern = "(?:6(?:[07-9][0-9]|3[024]|6[0-25])[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "60123456",
            postalPattern = "8[0-9]{4}",
            postalExample = "81257",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MF",
        value = CountryValidationRule(
            countryCode = "MF",
            dialCode = "590",
            nationalPrefix = "0",
            phonePattern = "(?:(?:69(?:0[0-9][0-9]|1(?:2[2-9]|3[0-5]))|7090[0-4])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "690001234",
            postalPattern = "9[78][01][0-9]{2}",
            postalExample = "97100",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MG",
        value = CountryValidationRule(
            countryCode = "MG",
            dialCode = "261",
            nationalPrefix = "0",
            phonePattern = "(?:3[2-9][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "321234567",
            postalPattern = "[0-9]{3}",
            postalExample = "501",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MH",
        value = CountryValidationRule(
            countryCode = "MH",
            dialCode = "692",
            nationalPrefix = "1",
            phonePattern = "(?:(?:(?:23|54)5|329|45[35-8])[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "2351234",
            postalPattern = "(969[67][0-9])(?:[ \\-]([0-9]{4}))?",
            postalExample = "96960",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MK",
        value = CountryValidationRule(
            countryCode = "MK",
            dialCode = "389",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:3555|(?:474|9[019]7)7)[0-9]{3}|7(?:[0-25-8][0-9][0-9]|3(?:[1-478][0-9]|6[01])|4(?:2[0-9]|60|7[01578])|9(?:[2-4][0-9]|5[01]|7[015]))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "72345678",
            postalPattern = "[0-9]{4}",
            postalExample = "1314",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ML",
        value = CountryValidationRule(
            countryCode = "ML",
            dialCode = "223",
            nationalPrefix = null,
            phonePattern = "(?:2(?:0(?:01|79)|17[0-9])[0-9]{4}|(?:5[0-3]|[679][0-9]|8[2-59])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "65012345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "MM",
        value = CountryValidationRule(
            countryCode = "MM",
            dialCode = "95",
            nationalPrefix = "0",
            phonePattern = "(?:(?:17[01]|9(?:2(?:[0-4]|[56][0-9][0-9])|(?:3(?:[0-36]|4[0-9])|(?:6[0-9]|8[89]|9[4-8])[0-9]|7(?:3|40|[5-9][0-9]))[0-9]|4(?:(?:[0245][0-9]|[1379])[0-9]|88)|5[0-6])[0-9])[0-9]{4}|9[69]1[0-9]{6}|9(?:[68][0-9]|9[089])[0-9]{5})",
            phoneLengths = listOf(7, 8, 9, 10),
            phoneExample = "92123456",
            postalPattern = "[0-9]{5}",
            postalExample = "11181",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MN",
        value = CountryValidationRule(
            countryCode = "MN",
            dialCode = "976",
            nationalPrefix = "0",
            phonePattern = "(?:(?:87[01]|92[0139])[0-9]{5}|(?:5[05]|6[069]|7[28]|8[0135689]|9[013-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "88123456",
            postalPattern = "[0-9]{5}",
            postalExample = "65030",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MO",
        value = CountryValidationRule(
            countryCode = "MO",
            dialCode = "853",
            nationalPrefix = null,
            phonePattern = "(?:6800[0-79][0-9]{3}|6(?:[235][0-9][0-9]|6(?:0[0-5]|[1-9][0-9])|8(?:0[1-9]|[14-8][0-9]|2[5-9]|[39][0-4]))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "66123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "MP",
        value = CountryValidationRule(
            countryCode = "MP",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:670(?:2(?:3[3-7]|56|8[4-8])|32[1-38]|4(?:33|8[348])|5(?:32|55|88)|6(?:64|70|82)|78[3589]|8[3-9]8|989)[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "6702345678",
            postalPattern = "(9695[012])(?:[ \\-]([0-9]{4}))?",
            postalExample = "96950",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MQ",
        value = CountryValidationRule(
            countryCode = "MQ",
            dialCode = "596",
            nationalPrefix = "0",
            phonePattern = "(?:(?:69[67][0-9][0-9]|7091[0-3])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "696201234",
            postalPattern = "9[78]2[0-9]{2}",
            postalExample = "97220",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MR",
        value = CountryValidationRule(
            countryCode = "MR",
            dialCode = "222",
            nationalPrefix = null,
            phonePattern = "(?:[2-4][0-46-9][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "22123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "MS",
        value = CountryValidationRule(
            countryCode = "MS",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:664(?:3(?:49|9[1-6])|49[2-6])[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "6644923456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "MT",
        value = CountryValidationRule(
            countryCode = "MT",
            dialCode = "356",
            nationalPrefix = null,
            phonePattern = "(?:(?:7(?:210|[79][0-9][0-9])|9(?:[29][0-9][0-9]|69[67]|8(?:1[1-3]|89|97)))[0-9]{4})",
            phoneLengths = listOf(8),
            phoneExample = "96961234",
            postalPattern = "[A-Z]{3} ?[0-9]{2,4}",
            postalExample = "NXR 01",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules6() {
    put(
        key = "MU",
        value = CountryValidationRule(
            countryCode = "MU",
            dialCode = "230",
            nationalPrefix = null,
            phonePattern = "(?:5(?:4(?:2[1-389]|7[1-9])|87[15-8])[0-9]{4}|(?:5(?:2[5-9]|4[3-689]|[57][0-9]|8[0-689]|9[0-8])|7(?:0[0-7]|3[013]))[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "52512345",
            postalPattern = "[0-9]{3}(?:[0-9]{2}|[A-Z]{2}[0-9]{3})",
            postalExample = "42602",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MV",
        value = CountryValidationRule(
            countryCode = "MV",
            dialCode = "960",
            nationalPrefix = null,
            phonePattern = "(?:(?:46[46]|[79][0-9][0-9])[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "7712345",
            postalPattern = "[0-9]{5}",
            postalExample = "20026",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MW",
        value = CountryValidationRule(
            countryCode = "MW",
            dialCode = "265",
            nationalPrefix = "0",
            phonePattern = "(?:111[0-9]{6}|(?:2[12]|31|77|[89][89])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "991234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "MX",
        value = CountryValidationRule(
            countryCode = "MX",
            dialCode = "52",
            nationalPrefix = null,
            phonePattern = "(?:(?:2(?:2[0-9]|3[1-35-8]|4[13-9]|7[1-689]|8[1-578]|9[467])|3(?:1[1-79]|[2458][1-9]|3[0-9]|7[1-8]|9[1-5])|4(?:1[1-57-9]|[267][1-9]|3[1-8]|[45][0-9]|8[1-35-9]|9[2-689])|5(?:[56][0-9]|88|9[1-79])|6(?:1[2-68]|[2-4][1-9]|5[1-36-9]|6[0-57-9]|7[1-7]|8[67]|9[4-8])|7(?:[1346][1-9]|[27][0-9]|5[13-9]|8[1-69]|9[17])|8(?:1[0-9]|2[13-689]|3[1-6]|4[124-6]|6[1246-9]|7[0-378]|9[12479])|9(?:1[346-9]|2[1-4]|3[2-46-8]|5[1348]|[69][0-9]|7[12]|8[1-8]))[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "2221234567",
            postalPattern = "[0-9]{5}",
            postalExample = "02860",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MY",
        value = CountryValidationRule(
            countryCode = "MY",
            dialCode = "60",
            nationalPrefix = "0",
            phonePattern = "(?:1(?:(?:1888[689]|4400|8(?:47|8[27])[0-4])[0-9]{4}|9[0-9]{7,8})|1(?:0(?:[23568][0-9]|4[0-6]|7[016-9]|9[0-8])|1(?:[1-5][0-9][0-9]|6(?:0[5-9]|[1-9][0-9])|7(?:[0-4][0-9]|5[0-79]|6[02-4]|8[02-5]))|(?:[26][0-9]|[37][1-9]|4[235-9])[0-9]|5(?:31|9[0-9][0-9])|8(?:1[23]|[236][0-9]|4[06]|5(?:46|[7-9])|7[016-9]|8[01]|9[0-8]))[0-9]{5})",
            phoneLengths = listOf(9, 10),
            phoneExample = "123456789",
            postalPattern = "[0-9]{5}",
            postalExample = "50450",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "MZ",
        value = CountryValidationRule(
            countryCode = "MZ",
            dialCode = "258",
            nationalPrefix = null,
            phonePattern = "(?:8[2-9][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "821234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1102",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NA",
        value = CountryValidationRule(
            countryCode = "NA",
            dialCode = "264",
            nationalPrefix = "0",
            phonePattern = "(?:(?:60|8[1245])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "811234567",
            postalPattern = "[0-9]{5}",
            postalExample = "10001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NC",
        value = CountryValidationRule(
            countryCode = "NC",
            dialCode = "687",
            nationalPrefix = null,
            phonePattern = "(?:(?:[579][0-9]|8[0-79])[0-9]{4})",
            phoneLengths = listOf(6),
            phoneExample = "751234",
            postalPattern = "988[0-9]{2}",
            postalExample = "98814",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NE",
        value = CountryValidationRule(
            countryCode = "NE",
            dialCode = "227",
            nationalPrefix = null,
            phonePattern = "(?:(?:23|7[0467]|[89][0-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "93123456",
            postalPattern = "[0-9]{4}",
            postalExample = "8001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NF",
        value = CountryValidationRule(
            countryCode = "NF",
            dialCode = "672",
            nationalPrefix = null,
            phonePattern = "(?:(?:14|3[58])[0-9]{4})",
            phoneLengths = listOf(6),
            phoneExample = "381234",
            postalPattern = "2899",
            postalExample = "2899",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NG",
        value = CountryValidationRule(
            countryCode = "NG",
            dialCode = "234",
            nationalPrefix = "0",
            phonePattern = "(?:(?:702[0-24-9]|819[01])[0-9]{6}|(?:7(?:0[13-9]|[12][0-9])|8(?:0[1-9]|1[0-8])|9(?:0[1-9]|1[1-6]))[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "8021234567",
            postalPattern = "[0-9]{6}",
            postalExample = "930283",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NI",
        value = CountryValidationRule(
            countryCode = "NI",
            dialCode = "505",
            nationalPrefix = null,
            phonePattern = "(?:(?:5(?:5[0-7]|[78][0-9])|6(?:20|3[035]|4[045]|5[05]|77|8[1-9]|9[059])|(?:7[5-8]|8[0-9])[0-9])[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "81234567",
            postalPattern = "[0-9]{5}",
            postalExample = "52000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NL",
        value = CountryValidationRule(
            countryCode = "NL",
            dialCode = "31",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6[1-58]|970[0-9])[0-9]{7})",
            phoneLengths = listOf(9, 11),
            phoneExample = "612345678",
            postalPattern = "[1-9][0-9]{3} ?(?:[A-RT-Z][A-Z]|S[BCE-RT-Z])",
            postalExample = "1234 AB",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NO",
        value = CountryValidationRule(
            countryCode = "NO",
            dialCode = "47",
            nationalPrefix = null,
            phonePattern = "(?:45(?:[0-24-9][0-9]|3[0-57-9])[0-9]{4}|(?:4[016-8]|9[0-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "40612345",
            postalPattern = "[0-9]{4}",
            postalExample = "0025",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NP",
        value = CountryValidationRule(
            countryCode = "NP",
            dialCode = "977",
            nationalPrefix = "0",
            phonePattern = "(?:9(?:00|6[0-3]|7[0-24-6]|8[0-24-68])[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "9841234567",
            postalPattern = "[0-9]{5}",
            postalExample = "44601",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "NR",
        value = CountryValidationRule(
            countryCode = "NR",
            dialCode = "674",
            nationalPrefix = null,
            phonePattern = "(?:(?:222|55[3-9]|666|777|8[0-9][0-9]|999)[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "5551234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "NU",
        value = CountryValidationRule(
            countryCode = "NU",
            dialCode = "683",
            nationalPrefix = null,
            phonePattern = "(?:(?:[56]|888[1-9])[0-9]{3})",
            phoneLengths = listOf(4, 7),
            phoneExample = "8884012",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "NZ",
        value = CountryValidationRule(
            countryCode = "NZ",
            dialCode = "64",
            nationalPrefix = "0",
            phonePattern = "(?:2(?:[0-27-9][0-9]|6)[0-9]{6,7}|2(?:1[0-9]|75)[0-9]{5})",
            phoneLengths = listOf(8, 9, 10),
            phoneExample = "211234567",
            postalPattern = "[0-9]{4}",
            postalExample = "6001",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "OM",
        value = CountryValidationRule(
            countryCode = "OM",
            dialCode = "968",
            nationalPrefix = null,
            phonePattern = "(?:(?:1505|90[1-9][0-9])[0-9]{4}|(?:7[124-9]|9[1-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "92123456",
            postalPattern = "(?:PC )?[0-9]{3}",
            postalExample = "133",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PA",
        value = CountryValidationRule(
            countryCode = "PA",
            dialCode = "507",
            nationalPrefix = null,
            phonePattern = "(?:(?:1[16]1|21[89]|6[0-9]{3}|8(?:1[01]|7[23]))[0-9]{4})",
            phoneLengths = listOf(7, 8),
            phoneExample = "61234567",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "PE",
        value = CountryValidationRule(
            countryCode = "PE",
            dialCode = "51",
            nationalPrefix = "0",
            phonePattern = "(?:9[0-9]{8})",
            phoneLengths = listOf(9),
            phoneExample = "912345678",
            postalPattern = "(?:LIMA [0-9]{1,2}|CALLAO 0?[0-9])|[0-2][0-9]{4}",
            postalExample = "LIMA 23",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PF",
        value = CountryValidationRule(
            countryCode = "PF",
            dialCode = "689",
            nationalPrefix = null,
            phonePattern = "(?:8[7-9][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "87123456",
            postalPattern = "987[0-9]{2}",
            postalExample = "98709",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PG",
        value = CountryValidationRule(
            countryCode = "PG",
            dialCode = "675",
            nationalPrefix = null,
            phonePattern = "(?:(?:7[0-9]|8[1-58])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "70123456",
            postalPattern = "[0-9]{3}",
            postalExample = "111",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PH",
        value = CountryValidationRule(
            countryCode = "PH",
            dialCode = "63",
            nationalPrefix = "0",
            phonePattern = "(?:(?:8(?:1[37]|9[5-8])|9(?:0[5-9]|1[0-24-9]|[235-7][0-9]|4[2-9]|8[135-9]|9[1-9]))[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "9051234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1008",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PK",
        value = CountryValidationRule(
            countryCode = "PK",
            dialCode = "92",
            nationalPrefix = "0",
            phonePattern = "(?:3(?:[0-247][0-9]|3[0-79]|55|64)[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "3012345678",
            postalPattern = "[0-9]{5}",
            postalExample = "44000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules7() {
    put(
        key = "PL",
        value = CountryValidationRule(
            countryCode = "PL",
            dialCode = "48",
            nationalPrefix = null,
            phonePattern = "(?:21(?:1[013-5]|2[0-9]|3[1-9])[0-9]{5}|(?:45|5[0137]|6[069]|7[2389]|88)[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "512345678",
            postalPattern = "[0-9]{2}-[0-9]{3}",
            postalExample = "00-950",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PM",
        value = CountryValidationRule(
            countryCode = "PM",
            dialCode = "508",
            nationalPrefix = "0",
            phonePattern = "(?:708(?:4[0-5]|5[0-6])[0-9]{4}|(?:[236-9][0-9]|4[02-489]|5[02-9])[0-9]{4})",
            phoneLengths = listOf(6, 9),
            phoneExample = "551234",
            postalPattern = "9[78]5[0-9]{2}",
            postalExample = "97500",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PR",
        value = CountryValidationRule(
            countryCode = "PR",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:(?:787|939)[2-9][0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "7872345678",
            postalPattern = "(00[679][0-9]{2})(?:[ \\-]([0-9]{4}))?",
            postalExample = "00930",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PS",
        value = CountryValidationRule(
            countryCode = "PS",
            dialCode = "970",
            nationalPrefix = "0",
            phonePattern = "(?:5[69][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "599123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "PT",
        value = CountryValidationRule(
            countryCode = "PT",
            dialCode = "351",
            nationalPrefix = null,
            phonePattern = "(?:6(?:[06]92(?:30|9[0-9])|[35]92(?:[049][0-9]|3[034]))[0-9]{3}|(?:(?:16|6[0356])93|9(?:[1-36][0-9][0-9]|480))[0-9]{5})",
            phoneLengths = listOf(9),
            phoneExample = "912345678",
            postalPattern = "[0-9]{4}-[0-9]{3}",
            postalExample = "2725-079",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PW",
        value = CountryValidationRule(
            countryCode = "PW",
            dialCode = "680",
            nationalPrefix = null,
            phonePattern = "(?:(?:(?:46|83)[0-5]|(?:6[2-4689]|78)0)[0-9]{4}|(?:45|77|88)[0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "6201234",
            postalPattern = "(969(?:39|40))(?:[ \\-]([0-9]{4}))?",
            postalExample = "96940",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "PY",
        value = CountryValidationRule(
            countryCode = "PY",
            dialCode = "595",
            nationalPrefix = "0",
            phonePattern = "(?:9(?:51|6[129]|7[1-6]|8[1-7]|9[1-5])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "961456789",
            postalPattern = "[0-9]{6}",
            postalExample = "001001",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "QA",
        value = CountryValidationRule(
            countryCode = "QA",
            dialCode = "974",
            nationalPrefix = null,
            phonePattern = "(?:[35-7][0-9]{7})",
            phoneLengths = listOf(8),
            phoneExample = "33123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "RE",
        value = CountryValidationRule(
            countryCode = "RE",
            dialCode = "262",
            nationalPrefix = "0",
            phonePattern = "(?:(?:69(?:2[0-9][0-9]|3(?:[06][0-6]|1[0-3]|2[0-2]|3[0-39]|4[0-9]|5[0-5]|7[0-37]|8[0-8]|9[0-479]))|7092[0-3])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "692123456",
            postalPattern = "9[78]4[0-9]{2}",
            postalExample = "97400",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "RO",
        value = CountryValidationRule(
            countryCode = "RO",
            dialCode = "40",
            nationalPrefix = "0",
            phonePattern = "(?:(?:630|702)0[0-9]{5}|(?:6(?:00|2[0-9])|7(?:0[013-9]|1[0-3]|[2-7][0-9]|8[03-8]|9[0-39]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "712034567",
            postalPattern = "[0-9]{6}",
            postalExample = "060274",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "RS",
        value = CountryValidationRule(
            countryCode = "RS",
            dialCode = "381",
            nationalPrefix = "0",
            phonePattern = "(?:6(?:[0-689]|7[0-9])[0-9]{6,7})",
            phoneLengths = listOf(8, 9, 10),
            phoneExample = "601234567",
            postalPattern = "[0-9]{5,6}",
            postalExample = "106314",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "RU",
        value = CountryValidationRule(
            countryCode = "RU",
            dialCode = "7",
            nationalPrefix = "8",
            phonePattern = "(?:9[0-9]{9})",
            phoneLengths = listOf(10),
            phoneExample = "9123456789",
            postalPattern = "[0-9]{6}",
            postalExample = "247112",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "RW",
        value = CountryValidationRule(
            countryCode = "RW",
            dialCode = "250",
            nationalPrefix = "0",
            phonePattern = "(?:7[237-9][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "720123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SA",
        value = CountryValidationRule(
            countryCode = "SA",
            dialCode = "966",
            nationalPrefix = "0",
            phonePattern = "(?:579[0-8][0-9]{5}|5(?:[013-689][0-9]|7[0-8])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "512345678",
            postalPattern = "[0-9]{5}",
            postalExample = "11564",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SB",
        value = CountryValidationRule(
            countryCode = "SB",
            dialCode = "677",
            nationalPrefix = null,
            phonePattern = "(?:48[0-9]{3}|(?:(?:6[89]|7[1-9]|8[4-9])[0-9]|9(?:1[2-9]|2[013-9]|3[0-2]|[46][0-9]|5[0-46-9]|7[0-689]|8[0-79]|9[0-8]))[0-9]{4})",
            phoneLengths = listOf(5, 7),
            phoneExample = "7421234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SC",
        value = CountryValidationRule(
            countryCode = "SC",
            dialCode = "248",
            nationalPrefix = null,
            phonePattern = "(?:2[125-8][0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "2510123",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SD",
        value = CountryValidationRule(
            countryCode = "SD",
            dialCode = "249",
            nationalPrefix = "0",
            phonePattern = "(?:(?:1[0-2]|9[0-3569])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "911231234",
            postalPattern = "[0-9]{5}",
            postalExample = "11042",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SE",
        value = CountryValidationRule(
            countryCode = "SE",
            dialCode = "46",
            nationalPrefix = "0",
            phonePattern = "(?:7[023689][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "701234567",
            postalPattern = "[0-9]{3} ?[0-9]{2}",
            postalExample = "11455",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SG",
        value = CountryValidationRule(
            countryCode = "SG",
            dialCode = "65",
            nationalPrefix = null,
            phonePattern = "(?:80[1-9][0-9]{5}|(?:8[1-9]|9[0-8])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "81234567",
            postalPattern = "[0-9]{6}",
            postalExample = "546080",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SH",
        value = CountryValidationRule(
            countryCode = "SH",
            dialCode = "290",
            nationalPrefix = null,
            phonePattern = "(?:[56][0-9]{4})",
            phoneLengths = listOf(5),
            phoneExample = "51234",
            postalPattern = "(?:ASCN|STHL) 1ZZ",
            postalExample = "STHL 1ZZ",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SI",
        value = CountryValidationRule(
            countryCode = "SI",
            dialCode = "386",
            nationalPrefix = "0",
            phonePattern = "(?:65(?:[178][0-9]|5[56]|6[01])[0-9]{4}|(?:[37][01]|4[013]|51|6[489])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "31234567",
            postalPattern = "[0-9]{4}",
            postalExample = "4000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SJ",
        value = CountryValidationRule(
            countryCode = "SJ",
            dialCode = "47",
            nationalPrefix = null,
            phonePattern = "(?:45(?:[0-24-9][0-9]|3[0-57-9])[0-9]{4}|(?:4[016-8]|9[0-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "41234567",
            postalPattern = "[0-9]{4}",
            postalExample = "9170",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SK",
        value = CountryValidationRule(
            countryCode = "SK",
            dialCode = "421",
            nationalPrefix = "0",
            phonePattern = "(?:909[1-9][0-9]{5}|9(?:0[1-8]|1[0-24-9]|4[03-57-9]|5[0-9])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "912123456",
            postalPattern = "[0-9]{3} ?[0-9]{2}",
            postalExample = "010 01",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SL",
        value = CountryValidationRule(
            countryCode = "SL",
            dialCode = "232",
            nationalPrefix = "0",
            phonePattern = "(?:(?:25|3[0-5]|66|7[0-9]|8[08]|9[09])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "25123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SM",
        value = CountryValidationRule(
            countryCode = "SM",
            dialCode = "378",
            nationalPrefix = null,
            phonePattern = "(?:6[16][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "66661212",
            postalPattern = "4789[0-9]",
            postalExample = "47890",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules8() {
    put(
        key = "SN",
        value = CountryValidationRule(
            countryCode = "SN",
            dialCode = "221",
            nationalPrefix = null,
            phonePattern = "(?:7(?:[015-8][0-9]|21|90)[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "701234567",
            postalPattern = "[0-9]{5}",
            postalExample = "12500",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SO",
        value = CountryValidationRule(
            countryCode = "SO",
            dialCode = "252",
            nationalPrefix = "0",
            phonePattern = "(?:(?:(?:15|(?:3[59]|4[89]|6[0-9]|7[0-35-9]|8[08])[0-9]|9(?:0[0-9]|[2-9]))[0-9]|2(?:4[0-9]|8))[0-9]{5}|(?:[67][0-9][0-9]|904)[0-9]{5})",
            phoneLengths = listOf(7, 8, 9),
            phoneExample = "71123456",
            postalPattern = "[A-Z]{2} ?[0-9]{5}",
            postalExample = "JH 09010",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SR",
        value = CountryValidationRule(
            countryCode = "SR",
            dialCode = "597",
            nationalPrefix = null,
            phonePattern = "(?:(?:6[08]|7[1-7]|8[1-9])[0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "7412345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SS",
        value = CountryValidationRule(
            countryCode = "SS",
            dialCode = "211",
            nationalPrefix = "0",
            phonePattern = "(?:(?:12|9[1257-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "977123456",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "ST",
        value = CountryValidationRule(
            countryCode = "ST",
            dialCode = "239",
            nationalPrefix = null,
            phonePattern = "(?:900[5-9][0-9]{3}|9(?:0[1-9]|[89][0-9])[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "9812345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SV",
        value = CountryValidationRule(
            countryCode = "SV",
            dialCode = "503",
            nationalPrefix = null,
            phonePattern = "(?:[5-7][0-9]{7})",
            phoneLengths = listOf(8),
            phoneExample = "70123456",
            postalPattern = "[1-3][1-7][0-2][0-9]",
            postalExample = "1101",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "SX",
        value = CountryValidationRule(
            countryCode = "SX",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:7215(?:1[02]|2[0-9]|5[034679]|8[0-24-8])[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "7215205678",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "SY",
        value = CountryValidationRule(
            countryCode = "SY",
            dialCode = "963",
            nationalPrefix = "0",
            phonePattern = "(?:(?:50|9[03-689])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "944567890",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = null,
        ),
    )
    put(
        key = "SZ",
        value = CountryValidationRule(
            countryCode = "SZ",
            dialCode = "268",
            nationalPrefix = null,
            phonePattern = "(?:7[5-9][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "76123456",
            postalPattern = "[HLMS][0-9]{3}",
            postalExample = "H100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TA",
        value = CountryValidationRule(
            countryCode = "TA",
            dialCode = "290",
            nationalPrefix = null,
            phonePattern = null,
            phoneLengths = listOf(),
            phoneExample = null,
            postalPattern = "TDCU 1ZZ",
            postalExample = "TDCU 1ZZ",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TC",
        value = CountryValidationRule(
            countryCode = "TC",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:649(?:2(?:3[129]|4[1-79])|3[0-9][0-9]|4[34][1-3])[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "6492311234",
            postalPattern = "TKCA 1ZZ",
            postalExample = "TKCA 1ZZ",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TD",
        value = CountryValidationRule(
            countryCode = "TD",
            dialCode = "235",
            nationalPrefix = null,
            phonePattern = "(?:(?:3[01]|[69][0-9]|77|8[5-7])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "63012345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "TG",
        value = CountryValidationRule(
            countryCode = "TG",
            dialCode = "228",
            nationalPrefix = null,
            phonePattern = "(?:(?:7[0-389]|9[0-36-9])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "90112345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "TH",
        value = CountryValidationRule(
            countryCode = "TH",
            dialCode = "66",
            nationalPrefix = "0",
            phonePattern = "(?:(?:(?:14|[89][0-9])[0-9][0-9]|6(?:[1-6][0-9][0-9]|7(?:1[0-8]|2[4-7]|3[1-6])))[0-9]{5})",
            phoneLengths = listOf(9),
            phoneExample = "812345678",
            postalPattern = "[0-9]{5}",
            postalExample = "10150",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TJ",
        value = CountryValidationRule(
            countryCode = "TJ",
            dialCode = "992",
            nationalPrefix = null,
            phonePattern = "(?:(?:33[03-9]|4(?:1[18]|4[02-479])|81[1-9])[0-9]{6}|(?:[09][0-9]|1[0-27-9]|2[0-27]|3[08]|40|5[05]|66|7[0157-9]|8[07-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "917123456",
            postalPattern = "[0-9]{6}",
            postalExample = "735450",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TK",
        value = CountryValidationRule(
            countryCode = "TK",
            dialCode = "690",
            nationalPrefix = null,
            phonePattern = "(?:7[2-4][0-9]{2,5})",
            phoneLengths = listOf(4, 5, 6, 7),
            phoneExample = "7290",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "TL",
        value = CountryValidationRule(
            countryCode = "TL",
            dialCode = "670",
            nationalPrefix = null,
            phonePattern = "(?:7[2-8][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "77212345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "TM",
        value = CountryValidationRule(
            countryCode = "TM",
            dialCode = "993",
            nationalPrefix = "8",
            phonePattern = "(?:(?:6[0-9]|7[12])[0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "66123456",
            postalPattern = "[0-9]{6}",
            postalExample = "744000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TN",
        value = CountryValidationRule(
            countryCode = "TN",
            dialCode = "216",
            nationalPrefix = null,
            phonePattern = "(?:3(?:001|[12]40)[0-9]{4}|(?:(?:[259][0-9]|4[0-8])[0-9]|3(?:1[1-35]|6[0-4]|91))[0-9]{5})",
            phoneLengths = listOf(8),
            phoneExample = "20123456",
            postalPattern = "[0-9]{4}",
            postalExample = "1002",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TO",
        value = CountryValidationRule(
            countryCode = "TO",
            dialCode = "676",
            nationalPrefix = null,
            phonePattern = "(?:(?:5(?:4[0-5]|5[4-6])|6(?:[09][0-9]|3[02]|8[15-9])|(?:7[0-9]|8[46-9])[0-9]|999)[0-9]{4})",
            phoneLengths = listOf(7),
            phoneExample = "7715123",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "TR",
        value = CountryValidationRule(
            countryCode = "TR",
            dialCode = "90",
            nationalPrefix = "0",
            phonePattern = "(?:5(?:61(?:011|61[0-9])|82[2-5][0-9][0-9])[0-9]{4}|5(?:[03-5][0-9]|1[06]|24|6[24]|7[245]|9[46])[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "5012345678",
            postalPattern = "[0-9]{5}",
            postalExample = "01960",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TT",
        value = CountryValidationRule(
            countryCode = "TT",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:868(?:(?:2[5-9]|3[0-9])[0-9]|4(?:3[0-6]|[6-9][0-9])|6(?:20|78|8[0-9])|7(?:0[1-9]|1[02-9]|[2-9][0-9]))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "8682911234",
            postalPattern = "[0-9]{6}",
            postalExample = "500234",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TV",
        value = CountryValidationRule(
            countryCode = "TV",
            dialCode = "688",
            nationalPrefix = null,
            phonePattern = "(?:(?:7[01][0-9]|90)[0-9]{4})",
            phoneLengths = listOf(6, 7),
            phoneExample = "901234",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "TW",
        value = CountryValidationRule(
            countryCode = "TW",
            dialCode = "886",
            nationalPrefix = "0",
            phonePattern = "(?:(?:40001[0-2]|9[0-8][0-9]{4})[0-9]{3})",
            phoneLengths = listOf(9),
            phoneExample = "912345678",
            postalPattern = "[0-9]{3}(?:[0-9]{2,3})?",
            postalExample = "104",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "TZ",
        value = CountryValidationRule(
            countryCode = "TZ",
            dialCode = "255",
            nationalPrefix = "0",
            phonePattern = "(?:(?:6[0-35-9]|7[0-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "621234567",
            postalPattern = "[0-9]{4,5}",
            postalExample = "6090",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
}

private fun MutableMap<String, CountryValidationRule>.putRules9() {
    put(
        key = "UA",
        value = CountryValidationRule(
            countryCode = "UA",
            dialCode = "380",
            nationalPrefix = "0",
            phonePattern = "(?:790[0-9]{6}|(?:39|50|6[36-8]|7[1-357]|9[1-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "501234567",
            postalPattern = "[0-9]{5}",
            postalExample = "15432",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "UG",
        value = CountryValidationRule(
            countryCode = "UG",
            dialCode = "256",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:28|34)0[0-9]{5}|7(?:[014-8][0-9]|2[01467]|3[0167]|9[0-589])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "712345678",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "US",
        value = CountryValidationRule(
            countryCode = "US",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:(?:472[2-47-9]|983[2-57-9])[0-9]{6}|(?:2(?:0[1-35-9]|1[02-9]|2[03-57-9]|3[1459]|4[08]|5[1-46]|6[0279]|7[02469]|8[13])|3(?:0[1-57-9]|1[02-9]|2[013-79]|3[0-24679]|4[167]|5[0-3]|6[01349]|8[056])|4(?:0[124-9]|1[02-579]|2[3-5]|3[0245]|4[023578]|58|6[349]|7[0589]|8[04])|5(?:0[1-57-9]|1[0235-8]|20|3[0149]|4[01]|5[179]|6[1-47]|7[0-5]|8[0256])|6(?:0[1-35-9]|1[024-9]|2[03689]|3[016]|4[0156]|5[01679]|6[0-279]|78|8[0-269])|7(?:0[1-46-8]|1[2-9]|2[04-8]|3[0-2478]|4[0378]|5[47]|6[02359]|7[0-59]|8[156])|8(?:0[1-68]|1[02-8]|2[0168]|3[0-2589]|4[03578]|5[046-9]|6[02-5]|7[028])|9(?:0[1346-9]|1[02-9]|2[0589]|3[0146-8]|4[01357-9]|5[12469]|7[0-3589]|8[04-69]))[2-9][0-9]{6})",
            phoneLengths = listOf(10),
            phoneExample = "2015550123",
            postalPattern = "([0-9]{5})(?:[ \\-]([0-9]{4}))?",
            postalExample = "95014",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "UY",
        value = CountryValidationRule(
            countryCode = "UY",
            dialCode = "598",
            nationalPrefix = "0",
            phonePattern = "(?:9[1-9][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "94231234",
            postalPattern = "[0-9]{5}",
            postalExample = "11600",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "UZ",
        value = CountryValidationRule(
            countryCode = "UZ",
            dialCode = "998",
            nationalPrefix = null,
            phonePattern = "(?:(?:(?:[25]0|33|8[078]|9[0-57-9])[0-9]{3}|6(?:1(?:2(?:2[01]|98)|35[0-4]|50[0-9]|61[23]|7(?:[01][017]|4[0-9]|55|9[5-9]))|2(?:(?:11|7[0-9])[0-9]|2(?:[12]1|9[01379])|5(?:[126][0-9]|3[0-4]))|5(?:19[01]|2(?:27|9[26])|(?:30|59|7[0-9])[0-9])|6(?:2(?:1[5-9]|2[0367]|38|41|52|60)|(?:3[79]|9[0-3])[0-9]|4(?:56|83)|7(?:[07][0-9]|1[017]|3[07]|4[047]|5[057]|67|8[0178]|9[79]))|7(?:2(?:24|3[237]|4[5-9]|7[15-8])|5(?:7[12]|8[0589])|7(?:0[0-9]|[39][07])|9(?:0[0-9]|7[079])))|7(?:[07][0-9]{3}|2(?:2(?:2[79]|95)|3(?:2[5-9]|6[0-6])|57[0-9]|7(?:0[0-9]|1[17]|2[27]|3[37]|44|5[057]|66|88))|3(?:2(?:1[0-6]|21|3[469]|7[159])|(?:33|9[4-6])[0-9]|5(?:0[0-4]|5[579]|9[0-9])|7(?:[0-3579][0-9]|4[0467]|6[67]|8[078]))|4(?:2(?:29|5[0257]|6[0-7]|7[1-57])|5(?:1[0-4]|8[0-9]|9[5-9])|7(?:0[0-9]|1[024589]|2[0-27]|3[0137]|[46][07]|5[01]|7[5-9]|9[079])|9(?:7[015-9]|[89][0-9]))|5(?:112|2(?:0[0-9]|2[29]|[49]4)|3[1568][0-9]|52[6-9]|7(?:0[01578]|1[017]|[23]7|4[047]|[5-7][0-9]|8[78]|9[079]))|9(?:22[128]|3(?:2[0-4]|7[0-9])|57[02569]|7(?:2[05-9]|3[37]|4[0-9]|60|7[2579]|87|9[07]))))[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "912345678",
            postalPattern = "[0-9]{6}",
            postalExample = "702100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VA",
        value = CountryValidationRule(
            countryCode = "VA",
            dialCode = "39",
            nationalPrefix = null,
            phonePattern = "(?:3[1-9][0-9]{8}|3[2-9][0-9]{7})",
            phoneLengths = listOf(9, 10),
            phoneExample = "3123456789",
            postalPattern = "00120",
            postalExample = "00120",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VC",
        value = CountryValidationRule(
            countryCode = "VC",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:784(?:4(?:3[0-5]|5[45]|89|9[0-8])|5(?:2[6-9]|3[0-4])|720)[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "7844301234",
            postalPattern = "VC[0-9]{4}",
            postalExample = "VC0100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VE",
        value = CountryValidationRule(
            countryCode = "VE",
            dialCode = "58",
            nationalPrefix = "0",
            phonePattern = "(?:4(?:1[24-8]|2[246])[0-9]{7})",
            phoneLengths = listOf(10),
            phoneExample = "4121234567",
            postalPattern = "[0-9]{4}",
            postalExample = "1010",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VG",
        value = CountryValidationRule(
            countryCode = "VG",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:284(?:245|3(?:0[0-3]|4[0-7]|68|9[34])|4(?:4[0-6]|68|9[69])|5(?:4[0-7]|68|9[69]))[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "2843001234",
            postalPattern = "VG[0-9]{4}",
            postalExample = "VG1110",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VI",
        value = CountryValidationRule(
            countryCode = "VI",
            dialCode = "1",
            nationalPrefix = "1",
            phonePattern = "(?:340(?:2(?:0[0-9]|10|2[06-8]|4[49]|77)|3(?:32|44)|4(?:2[23]|44|7[34]|89)|5(?:1[34]|55)|6(?:2[56]|4[23]|77|9[023])|7(?:1[2-57-9]|2[57]|7[0-9])|884|998)[0-9]{4})",
            phoneLengths = listOf(10),
            phoneExample = "3406421234",
            postalPattern = "(008(?:(?:[0-4][0-9])|(?:5[01])))(?:[ \\-]([0-9]{4}))?",
            postalExample = "00802-1222",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VN",
        value = CountryValidationRule(
            countryCode = "VN",
            dialCode = "84",
            nationalPrefix = "0",
            phonePattern = "(?:121[0-3][0-9]{5}|(?:160|(?:3[0-9]|7[06-9])[0-9]|5(?:[1689][0-9]|2[238]|59)|8(?:[1-8][0-9]|9[6-9])|9(?:[0-8][0-9]|9[013-9]))[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "912345678",
            postalPattern = "[0-9]{5}[0-9]?",
            postalExample = "70010",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "VU",
        value = CountryValidationRule(
            countryCode = "VU",
            dialCode = "678",
            nationalPrefix = null,
            phonePattern = "(?:(?:[58][0-9]|7[0-7])[0-9]{5})",
            phoneLengths = listOf(7),
            phoneExample = "5912345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "WF",
        value = CountryValidationRule(
            countryCode = "WF",
            dialCode = "681",
            nationalPrefix = null,
            phonePattern = "(?:(?:72|8[23])[0-9]{4})",
            phoneLengths = listOf(6),
            phoneExample = "821234",
            postalPattern = "986[0-9]{2}",
            postalExample = "98600",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "WS",
        value = CountryValidationRule(
            countryCode = "WS",
            dialCode = "685",
            nationalPrefix = null,
            phonePattern = "(?:(?:7[1-35-8]|8(?:[3-7]|9[0-9]{3}))[0-9]{5})",
            phoneLengths = listOf(7, 10),
            phoneExample = "7212345",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "XK",
        value = CountryValidationRule(
            countryCode = "XK",
            dialCode = "383",
            nationalPrefix = "0",
            phonePattern = "(?:4[3-9][0-9]{6})",
            phoneLengths = listOf(8),
            phoneExample = "43201234",
            postalPattern = "[1-7][0-9]{4}",
            postalExample = "10000",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "YE",
        value = CountryValidationRule(
            countryCode = "YE",
            dialCode = "967",
            nationalPrefix = "0",
            phonePattern = "(?:7[01378][0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "712345678",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
    put(
        key = "YT",
        value = CountryValidationRule(
            countryCode = "YT",
            dialCode = "262",
            nationalPrefix = "0",
            phonePattern = "(?:(?:639(?:0[0-79]|1[019]|[267][0-9]|3[09]|40|5[05-9]|9[04-79])|7093[5-7])[0-9]{4})",
            phoneLengths = listOf(9),
            phoneExample = "639012345",
            postalPattern = "976[0-9]{2}",
            postalExample = "97600",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ZA",
        value = CountryValidationRule(
            countryCode = "ZA",
            dialCode = "27",
            nationalPrefix = "0",
            phonePattern = "(?:(?:1(?:3492[0-25]|4495[0235]|549(?:20|5[01]))|4[34]492[01])[0-9]{3}|8[1-4][0-9]{3,7}|(?:2[27]|47|54)4950[0-9]{3}|(?:1(?:049[2-4]|9[12][0-9][0-9])|(?:50[0-2]|[67][0-9][0-9])[0-9][0-9]|8(?:5[0-9]{3}|7(?:08[67]|158|28[5-9]|310)))[0-9]{4}|(?:1[6-8]|28|3[2-69]|4[025689]|5[36-8])4920[0-9]{3}|(?:12|[2-5]1)492[0-9]{4})",
            phoneLengths = listOf(5, 6, 7, 8, 9),
            phoneExample = "711234567",
            postalPattern = "[0-9]{4}",
            postalExample = "0083",
            isPostalRequired = true,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ZM",
        value = CountryValidationRule(
            countryCode = "ZM",
            dialCode = "260",
            nationalPrefix = "0",
            phonePattern = "(?:(?:[59][5-8]|7[5-9])[0-9]{7})",
            phoneLengths = listOf(9),
            phoneExample = "955123456",
            postalPattern = "[0-9]{5}",
            postalExample = "50100",
            isPostalRequired = false,
            hasPostalSystem = true,
        ),
    )
    put(
        key = "ZW",
        value = CountryValidationRule(
            countryCode = "ZW",
            dialCode = "263",
            nationalPrefix = "0",
            phonePattern = "(?:7(?:[1278][0-9]|3[1-9]|9[01])[0-9]{6})",
            phoneLengths = listOf(9),
            phoneExample = "712345678",
            postalPattern = null,
            postalExample = null,
            isPostalRequired = false,
            hasPostalSystem = false,
        ),
    )
}
