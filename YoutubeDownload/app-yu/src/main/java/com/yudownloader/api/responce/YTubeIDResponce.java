package com.yudownloader.api.responce;

import com.yudownloader.api.model.YTubeIDModel;
import com.yudownloader.api.model.YTubeRefreshTotalModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class YTubeIDResponce {

    @SerializedName("nextPageToken")
    public String nextPageToken;

    @SerializedName("items")
    public ArrayList<YTubeIDModel> items;

    @SerializedName("pageInfo")
    public YTubeRefreshTotalModel pageInfo;


}
