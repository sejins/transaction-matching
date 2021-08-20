package com.jingeore.zone;

import lombok.Data;

@Data
public class ZoneForm {

    private String zoneName;

    public String getLocalNameOfCity(){ return zoneName.substring(0,zoneName.indexOf("(")); }

    public String getCityName(){ return zoneName.substring(zoneName.indexOf("(")+1, zoneName.indexOf(")")); }

    public String getProvinceName(){ return zoneName.substring(zoneName.indexOf("/")+1); }
}
