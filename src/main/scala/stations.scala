package allaboard

object Station {
  def unapply(name: String) =
    if (names(name.toUpperCase)) Some(name)
    else None

  val names = Set.empty[String] + "AM" + "AB" + "AZ" + "AH" + "AS" +
    "AN" + "AP" + "AO" + "AC" + "AV" + "BI" + "BH" + "MC" + "BS" + "BY" +
    "BV" + "BM" + "BN" + "BK" + "BB" + "BU" + "BW" + "BF" + "CB" +
    "CM" + "CY" + "IF" + "CN" + "XC" + "DL" + "DV" + "DO" + "DN" + "EO" +
    "ED" + "EH" + "EL" + "EZ" + "EN" + "EX" + "FW" + "FH" + "GD" +
    "GW" + "GI" + "GL" + "GG" + "GK" + "RS" + "HQ" + "HL" + "HN" + "RM"+
    "HW" + "HZ" + "HG" + "HI" + "HD" + "UF" + "HB" + "JA" + "KG" +
    "HP" + "ON" + "LP" + "LI" + "LW" + "FA" + "LS" + "LB" + "LN" + "LY" +
    "MA" + "MZ" + "SQ" + "MW" + "MP" + "MU" + "MI" + "MD" + "MB" +
    "GO" + "MK" + "HS" + "UV" + "ZM" + "MX" + "MR" + "HV" + "OL" + "TB" +
    "MS" + "ML" + "MT" + "MV" + "MH" + "NN" + "NT" + "NE" + "NH" +
    "NB" + "NV" + "NY" + "NA" + "ND" + "NP" + "OR" + "NZ" + "OD" + "OG" +
    "OS" + "PV" + "PS" + "RN" + "PC" + "PQ" + "PE" + "PH" + "PF" +
    "PL" + "PP" + "PO" + "PR" + "PJ" + "FZ" + "RH" + "RY" + "17" + "RA" +
    "RB" + "RW" + "RG" + "RL" + "RF" + "CW" + "TS" + "SE" + "RT" +
    "XG" + "SM" + "CH" + "SO" + "LA" + "SV" + "SG" + "SF" + "ST" + "TE" +
    "TO" + "TR" + "TC" + "US" + "UM" + "WK" + "WA" + "WG" + "WT" +
    "23" + "WF" + "WW" + "WH" + "WR" + "WB" + "WL"
}
