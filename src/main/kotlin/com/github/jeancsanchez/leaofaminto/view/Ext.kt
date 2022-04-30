package com.github.jeancsanchez.leaofaminto.view

import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * @author @jeancsanchez
 * @created 17/05/2021
 * Jesus loves you.
 */

/**
 * Formata [LocalDate] para string no formato brasileiro
 * Ex: 2021-08-01 --> 01/08/2021
 */
fun LocalDate.formatToStringBR(): String {
    var day = dayOfMonth.toString()
    var month = monthValue.toString()

    if (dayOfMonth < 10) {
        day = "0".plus(dayOfMonth)
    }

    if (monthValue < 10) {
        month = "0".plus(monthValue)
    }

    return "$day/$month/$year"
}

/**
 * Formata string para o formato [LocalDate]
 */
fun String.formatStringBRToDate(): LocalDate? {
    if (trim().contains("/")) {
        return try {
            val arr = split("/")
            val day = arr[0].trim()
            val month = arr[1].trim()
            val year = arr[2].trim()

            LocalDate.of(year.toInt(), month.toInt(), day.toInt())
        } catch (t: Throwable) {
            null
        }
    }

    return null
}

/**
 * Formata um string timestamp para o formato [LocalDate]
 */
fun String.formatStringBRFromTimeStamp(): LocalDate {
    return ZonedDateTime.parse(this).toLocalDate()
}

/**
 * Extrai o tipo do ativo
 */
fun String.extractTipoDeAtivo(): TipoDeAtivo {
    val codigo = this.trim()
    if (codigo.contains("FII", true) || isFII(codigo)) {
        return TipoDeAtivo.FII
    }

    return TipoDeAtivo.ACAO
}

/**
 * Extrai o codigo do ativo
 */
fun String.extractCodigoAtivo(): String {
    if (trim().last().equals('F', true)) {
        return this.toUpperCase().dropLast(1)
    }

    return this.toUpperCase()
}

/**
 * Extrai o codigo do ativo do arquivo de movimentações v2 do portal CEI
 */
fun String.extractCodigoAtivoV2(): String {
    return this.split("-")[0].trim().toUpperCase()
}

/**
 * Extrai o nome da corretora
 */
fun String.extractNomeCorretora(): String {
    if (contains("Clear", true)) {
        return "Clear"
    }

    return this
}

/**
 * Arredonda o número "para cima"
 */
fun Double.round(): Double {
    return BigDecimal(this)
        .setScale(2, RoundingMode.HALF_EVEN)
        .toDouble()
}

/**
 * Converte valor para o formato de moeda brasileira
 */

fun Double.toBrazilMoney(): String {
    val str = "R$ ${round()}".replace(".", ",")
    if (str.split(",")[1] == "0") {
        return str.plus("0")
    }

    return str
}

fun Double.toQuantidadeString(): String {
    val isExact = this.toString().split(".")[1].toLong() == 0L
    if (this == 0.0 || isExact) return toInt().toString()
    return toString()
}

fun String.stripToDouble(): Double = replace("R$ ", "")
    .replace(",", ".")
    .toDouble()

fun isFII(codigoFII: String): Boolean {
    return codigoFII.dropLast(2) in listOf(
        "ITIT",
        "LAVF",
        "AFHI",
        "AFOF",
        "MTOF",
        "ALZR",
        "AIEC",
        "BCRI",
        "BNFS",
        "BZEL",
        "BPLC",
        "BBFI",
        "BBFO",
        "BBPO",
        "BBIM",
        "BBRC",
        "RDPD",
        "RNDP",
        "BLCP",
        "BLCA",
        "BLMC",
        "BLMG",
        "BLMO",
        "BLMR",
        "BROL",
        "BCIA",
        "BREV",
        "BZLI",
        "CARE",
        "BRCO",
        "BICE",
        "BIME",
        "BRIM",
        "BRIP",
        "BIPD",
        "LLAO",
        "BTLG",
        "BTWR",
        "BTSG",
        "BTSI",
        "CRFF",
        "CXRI",
        "CCRF",
        "CPFF",
        "CPTS",
        "CACR",
        "CBOP",
        "CFHI",
        "CJCT",
        "CORM",
        "HGFF",
        "HGLG",
        "HGPO",
        "HGRE",
        "HGCR",
        "HGRU",
        "HGRS",
        "CVPR",
        "CYCR",
        "DLMT",
        "DEVA",
        "DAMT",
        "DOVL",
        "EGYR",
        "EQIR",
        "ERCR",
        "ERPA",
        "KEVE",
        "KINP",
        "EXES",
        "FLCR",
        "VRTA",
        "BMII",
        "LRDI",
        "MCHY",
        "MMPD",
        "IBCR",
        "GAME",
        "BTCR",
        "MTRS",
        "ANCR",
        "FAED",
        "BMLC",
        "BPRP",
        "BRCR",
        "FEXC",
        "BCFF",
        "FCFL",
        "CNES",
        "CEOC",
        "FAMB",
        "EDGA",
        "ELDO",
        "FLRP",
        "HCRI",
        "NSLU",
        "HTMX",
        "MAXR",
        "NVHO",
        "PQDP",
        "PATB",
        "RBRM",
        "RBRR",
        "RECR",
        "RECT",
        "JRDM",
        "SHDP",
        "SAIC",
        "TBOF",
        "ALMI",
        "TRNT",
        "VLOL",
        "EQIN",
        "OUFF",
        "WTSP",
        "RECH",
        "VVPR",
        "LVBI",
        "BARI",
        "BRHT",
        "BPFF",
        "BVAR",
        "BPML",
        "BTRA",
        "CXCI",
        "CXCE",
        "CXTL",
        "CTXT",
        "CJFI",
        "FLMA",
        "EDFO",
        "EURO",
        "GESE",
        "ABCP",
        "GTWR",
        "HBTT",
        "HUCG",
        "HUSC",
        "FIIB",
        "FINF",
        "FMOF",
        "MGFF",
        "MVFI",
        "NPAR",
        "OULG",
        "PABY",
        "FPNG",
        "ESTQ",
        "VPSI",
        "FPAB",
        "RBRY",
        "RBRP",
        "RCRB",
        "RBED",
        "RBVA",
        "RNGO",
        "SFND",
        "FISC",
        "SCPF",
        "SDIL",
        "SHPH",
        "TGAR",
        "ONEF",
        "VERE",
        "FVPQ",
        "FIVN",
        "VTLT",
        "VSEC",
        "VSHO",
        "IDFI",
        "IBFF",
        "MGIM",
        "PLCR",
        "VTPL",
        "RELG",
        "CVBI",
        "MCCI",
        "FGPM",
        "ATWN",
        "ARRI",
        "BTAL",
        "CXCO",
        "HOSI",
        "MGHT",
        "RECX",
        "PVBI",
        "DVFF",
        "MGCR",
        "RFOF",
        "VTPA",
        "VTXI",
        "HBRH",
        "FDHY",
        "IRDM",
        "SBCL",
        "SPVJ",
        "KFOF",
        "OURE",
        "BLUR",
        "LKDV",
        "RBRI",
        "FATN",
        "BRLA",
        "CXAG",
        "HBCR",
        "MINT",
        "RZTR",
        "ROOF",
        "TCIN",
        "GCFF",
        "GCRI",
        "GZIT",
        "GSFI",
        "FIGS",
        "GGRC",
        "GLPL",
        "RCFA",
        "GTLG",
        "GALG",
        "HABT",
        "ATCR",
        "HCTR",
        "HCST",
        "HCPR",
        "HCHG",
        "HAAA",
        "ATSA",
        "HGBS",
        "HLOG",
        "HDOF",
        "HRDF",
        "HREC",
        "SEED",
        "HPDP",
        "HFOF",
        "HGIC",
        "HSAF",
        "HSLG",
        "HSML",
        "HSRE",
        "HUSI",
        "ITIP",
        "BICR",
        "IRIM",
        "JCDA",
        "JBFO",
        "VJFD",
        "JFLL",
        "JPPA",
        "JPPC",
        "JSAF",
        "JSRE",
        "JTPR",
        "KISU",
        "KIVO",
        "KCRE",
        "KNHY",
        "KNRE",
        "KNIP",
        "KNRI",
        "KNCR",
        "KNSC",
        "LPLP",
        "LATR",
        "LASC",
        "LSPA",
        "LOFT",
        "LFTT",
        "LGCP",
        "LUGG",
        "MALL",
        "MMVE",
        "MADS",
        "MCHF",
        "MXRF",
        "MFII",
        "MFAI",
        "MGLG",
        "MGLC",
        "MATV",
        "MORE",
        "MORC",
        "PRTS",
        "SHOP",
        "DRIT",
        "MOFF",
        "NAVT",
        "APTO",
        "NEWL",
        "NEWU",
        "NVIF",
        "FTCE",
        "OUJP",
        "ORPD",
        "PNDL",
        "PNLN",
        "PNPR",
        "VTVI",
        "PQAG",
        "PATC",
        "PATL",
        "PEMA",
        "PRSN",
        "PLOG",
        "PURB",
        "PORD",
        "PLRI",
        "PRZS",
        "PRSV",
        "PBLV",
        "QAGR",
        "QAMI",
        "QIRI",
        "XBXO",
        "RSPD",
        "RBDS",
        "RBIR",
        "RBLG",
        "RBCO",
        "RRCI",
        "FIIP",
        "RBRD",
        "RBRU",
        "RBTS",
        "RBRF",
        "RCFF",
        "RBRL",
        "RPRI",
        "RMAI",
        "RBHG",
        "RBHY",
        "RBVO",
        "RBFF",
        "RBRS",
        "RZAK",
        "ARCT",
        "SADI",
        "SARE",
        "SACL",
        "FISD",
        "SFRO",
        "SEQR",
        "SRVD",
        "WPLZ",
        "SIGR",
        "REIT",
        "SJAU",
        "SOLR",
        "SPTW",
        "SPAF",
        "STRX",
        "SNFF",
        "SNCI",
        "TELD",
        "TELF",
        "TEPP",
        "TSER",
        "TJKB",
        "TORD",
        "TSNC",
        "TCPF",
        "XTED",
        "TRXF",
        "TRXB",
        "TSNM",
        "URPR",
        "VGIR",
        "VGIP",
        "VGHF",
        "EVBI",
        "RVBI",
        "VCJR",
        "VCRR",
        "VLJS",
        "SALI",
        "VSLH",
        "VIDS",
        "VIUR",
        "VIFI",
        "VILG",
        "VINO",
        "VISC",
        "VOTS",
        "VXXV",
        "WHGR",
        "XPCM",
        "XPCI",
        "XPHT",
        "XPIN",
        "XPLG",
        "XPML",
        "XPPR",
        "XPSF",
        "YUFI",
        "ZIFI"
    )
}