package com.ranker.countries.data.worldbank

/**
 * A World Bank indicator the publisher may pick, and how to show it. The figures themselves are
 * fetched when publishing; only this description lives in the app. Values from the API are
 * multiplied by [scale] to get [unit], e.g. people to millions.
 */
internal data class Indicator(
    val code: String,
    val title: String,
    val emoji: String,
    /** What is measured, for the source line under the title. */
    val measure: String,
    val unit: String,
    val decimals: Int,
    val scale: Double = 1.0,
) {
    /** The stat's id, and so its Firestore document's: SP.POP.TOTL is `sp-pop-totl`. */
    val id: String get() = code.lowercase().replace('.', '-')
}

/**
 * The pool each publish draws from. Each one ranks well: recent data for 150+ countries and no wall
 * of ties at the top (internet use and urban share, both 100% for many, are left out for that).
 */
internal object Indicators {
    val all = listOf(
        Indicator("SP.POP.TOTL", "Crowded nations", "👥", "Population, millions", "M", 0, scale = 1e-6),
        Indicator("SP.DYN.LE00.IN", "Long lives", "🕰️", "Life expectancy at birth", "yrs", 1),
        Indicator("SP.DYN.TFRT.IN", "Big families", "👶", "Births per woman", "", 1),
        Indicator("SP.POP.65UP.TO.ZS", "Silver nations", "👵", "Share aged 65 and over", "%", 1),
        Indicator("SP.POP.0014.TO.ZS", "Young nations", "🧒", "Share aged 14 and under", "%", 1),
        Indicator("SP.POP.GROW", "Fastest growing", "📈", "Population growth per year", "%", 2),
        Indicator("SP.DYN.CBRT.IN", "Baby booms", "🍼", "Births per 1,000 people", "", 1),
        Indicator("EN.POP.DNST", "Packed tight", "🏙️", "People per km² of land", "", 0),
        Indicator("SM.POP.TOTL.ZS", "Melting pots", "🧳", "Share born abroad", "%", 1),
        Indicator("AG.SRF.TOTL.K2", "Giant lands", "🗺️", "Surface area, thousand km²", "k km²", 0, scale = 1e-3),
        Indicator("AG.LND.FRST.ZS", "Forest cover", "🌳", "Share of land under forest", "%", 1),
        Indicator("AG.LND.ARBL.ZS", "Farmland", "🌾", "Share of land that is arable", "%", 1),
        Indicator("AG.LND.PRCP.MM", "Rainiest lands", "🌧️", "Average rainfall per year", "mm", 0),
        Indicator("ER.PTD.TOTL.ZS", "Protected nature", "🦜", "Share of land and sea protected", "%", 1),
        Indicator("EN.GHG.CO2.PC.CE.AR5", "Carbon footprints", "🏭", "CO₂ per person, tonnes", "t", 1),
        Indicator("EG.FEC.RNEW.ZS", "Green energy", "♻️", "Renewables' share of energy used", "%", 1),
        Indicator("EG.USE.PCAP.KG.OE", "Energy hungry", "⚡", "Energy use per person, kg of oil", "kg", 0),
        Indicator("NY.GDP.PCAP.CD", "Richest per person", "💰", "GDP per person, thousand US$", "k$", 1, scale = 1e-3),
        Indicator("NY.GDP.MKTP.CD", "Biggest economies", "🏦", "GDP, billion US$", "bn$", 0, scale = 1e-9),
        Indicator("NY.GDP.MKTP.KD.ZG", "Growth sprint", "🚀", "Economic growth in a year", "%", 1),
        Indicator("FP.CPI.TOTL.ZG", "Rising prices", "🎈", "Consumer price inflation in a year", "%", 1),
        Indicator("SL.UEM.TOTL.ZS", "Out of work", "📋", "Unemployment, share of workforce", "%", 1),
        Indicator("SL.TLF.CACT.FE.ZS", "Women at work", "👩‍💼", "Share of women in the workforce", "%", 1),
        Indicator("SG.GEN.PARL.ZS", "Women in parliament", "🏛️", "Share of seats held by women", "%", 1),
        Indicator("SE.XPD.TOTL.GD.ZS", "School budgets", "🎓", "Education spending, share of GDP", "%", 1),
        Indicator("NE.TRD.GNFS.ZS", "Trade hubs", "🚢", "Trade, share of GDP", "%", 0),
        Indicator("BX.TRF.PWKR.DT.GD.ZS", "Money from home", "💸", "Remittances received, share of GDP", "%", 1),
        Indicator("IT.CEL.SETS.P2", "Phone crazy", "📱", "Mobile subscriptions per 100 people", "", 0),
        Indicator("IS.AIR.PSGR", "Busy skies", "✈️", "Air passengers carried, millions", "M", 1, scale = 1e-6),
        Indicator("TX.VAL.TECH.MF.ZS", "High-tech exporters", "💻", "High-tech share of manufactured exports", "%", 1),
    )
}
