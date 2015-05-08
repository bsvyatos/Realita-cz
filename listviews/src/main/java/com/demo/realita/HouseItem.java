package com.demo.realita;

import android.os.Parcelable;
import android.os.Parcel;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;


public class HouseItem implements Parcelable {
    public String Id;
    public String mAddress;
    // Sale or rent
    public OfferType mOfferType;
    // Sale or rent
    public EstateType mPropertyType;
    // 2+1, etc
    public int mLayout;
    public int mOwnership;
    public String mEnergyType;
    public int mBuildingType;
    public int mEquipment;
    public String mDescription;
    public String mFullDescription = "";
    public double mSize;
    public int mPrice;
    public int mFees;
    public int mFloor;
    public int mZipCode;
    public String mImgPreview;
    public String mHouseInfo;
    public boolean mBalkony;
    public boolean mTerrace;

    public HouseItem(JsonElement item) {
        this.Id = item.getAsJsonObject().getAsJsonPrimitive("id").getAsString();
        this.mAddress = item.getAsJsonObject().getAsJsonPrimitive("mAddress").getAsString();
        if (!(item.getAsJsonObject().get("mOfferType") instanceof JsonNull))
            this.mOfferType = OfferType.values()[item.getAsJsonObject().getAsJsonPrimitive("mOfferType").getAsInt()];
        if (!(item.getAsJsonObject().get("mPropertyType") instanceof JsonNull))
            this.mPropertyType = EstateType.values()[item.getAsJsonObject().getAsJsonPrimitive("mPropertyType").getAsInt()];
        else
            this.mPropertyType = EstateType.values()[0];
        if (!(item.getAsJsonObject().get("mLayout") instanceof JsonNull))
            this.mLayout = item.getAsJsonObject().getAsJsonPrimitive("mLayout").getAsInt();
        if (!(item.getAsJsonObject().get("mOwnership") instanceof JsonNull))
            this.mOwnership = item.getAsJsonObject().getAsJsonPrimitive("mOwnership").getAsInt();
        if (!(item.getAsJsonObject().get("mEnergyType") instanceof JsonNull))
            this.mEnergyType = item.getAsJsonObject().getAsJsonPrimitive("mEnergyType").getAsString();
        if (!(item.getAsJsonObject().get("mBuildingType") instanceof JsonNull))
            this.mBuildingType = item.getAsJsonObject().getAsJsonPrimitive("mBuildingType").getAsInt();
        if (!(item.getAsJsonObject().get("mEquipment") instanceof JsonNull))
            this.mEquipment = item.getAsJsonObject().getAsJsonPrimitive("mEquipment").getAsInt();
        if (!(item.getAsJsonObject().get("mDescription") instanceof JsonNull))
            this.mDescription = item.getAsJsonObject().getAsJsonPrimitive("mDescription").getAsString();
        if (!(item.getAsJsonObject().get("mFullDescription") instanceof JsonNull))
            this.mFullDescription = item.getAsJsonObject().getAsJsonPrimitive("mFullDescription").getAsString();
        if (!(item.getAsJsonObject().get("mSize") instanceof JsonNull))
            this.mSize = item.getAsJsonObject().getAsJsonPrimitive("mSize").getAsInt();
        if (!(item.getAsJsonObject().get("mPrice") instanceof JsonNull))
            this.mPrice = item.getAsJsonObject().getAsJsonPrimitive("mPrice").getAsInt();
        if (!(item.getAsJsonObject().get("mFees") instanceof JsonNull))
            this.mFees = item.getAsJsonObject().getAsJsonPrimitive("mFees").getAsInt();
        if (!(item.getAsJsonObject().get("mFloor") instanceof JsonNull))
            this.mFloor = item.getAsJsonObject().getAsJsonPrimitive("mFloor").getAsInt();
        if (!(item.getAsJsonObject().get("mZipCode") instanceof JsonNull))
            this.mZipCode = item.getAsJsonObject().getAsJsonPrimitive("mZipCode").getAsInt();
        if (!(item.getAsJsonObject().get("mImgPreview") instanceof JsonNull))
            this.mImgPreview = item.getAsJsonObject().getAsJsonPrimitive("mImgPreview").getAsString();
        //this.mHouseInfo = item.getAsJsonObject().getAsJsonPrimitive("mHouseInfo").getAsString();
        if (!(item.getAsJsonObject().get("mBalkony") instanceof JsonNull))
            this.mBalkony = item.getAsJsonObject().getAsJsonPrimitive("mBalkony").getAsBoolean();
        if (!(item.getAsJsonObject().get("mTerrace") instanceof JsonNull))
            this.mTerrace = item.getAsJsonObject().getAsJsonPrimitive("mTerrace").getAsBoolean();
    }

    public HouseItem(Parcel in){
        this.Id = in.readString();
        this.mAddress = in.readString();
        this.mOfferType = OfferType.values()[in.readInt()];
        this.mPropertyType = EstateType.values()[in.readInt()];
        this.mLayout = in.readInt();
        this.mOwnership = in.readInt();
        this.mEnergyType = in.readString();
        this.mBuildingType = in.readInt();
        this.mEquipment = in.readInt();
        this.mDescription = in.readString();
        this.mFullDescription = in.readString();
        this.mSize = in.readDouble();
        this.mPrice = in.readInt();
        this.mFees = in.readInt();
        this.mFloor = in.readInt();
        this.mZipCode = in.readInt();
        this.mImgPreview = in.readString();
        this.mHouseInfo = in.readString();
        this.mBalkony = in.readByte() != 0;
        this.mTerrace = in.readByte() != 0;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Id);
        parcel.writeString(mAddress);
        parcel.writeInt(mOfferType.ordinal());
        parcel.writeInt(mPropertyType.ordinal());
        parcel.writeInt(mLayout);
        parcel.writeInt(mOwnership);
        parcel.writeString(mEnergyType);
        parcel.writeInt(mBuildingType);
        parcel.writeInt(mEquipment);
        parcel.writeString(mDescription);
        parcel.writeString(mFullDescription);
        parcel.writeDouble(mSize);
        parcel.writeInt(mPrice);
        parcel.writeInt(mFees);
        parcel.writeInt(mFloor);
        parcel.writeInt(mZipCode);
        parcel.writeString(mImgPreview);
        parcel.writeString(mHouseInfo);
        parcel.writeByte((byte) (mBalkony ? 1 : 0));
        parcel.writeByte((byte) (mTerrace ? 1 : 0));

    }

    public static final Parcelable.Creator<HouseItem> CREATOR = new Parcelable.Creator<HouseItem>() {

        public HouseItem createFromParcel(Parcel in) {
            return new HouseItem(in);
        }

        public HouseItem[] newArray(int size) {
            return new HouseItem[size];
        }
    };


}