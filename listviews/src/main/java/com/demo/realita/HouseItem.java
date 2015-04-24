package com.demo.realita;

import android.os.Parcelable;
import android.os.Parcel;

import com.google.gson.JsonElement;

/**
 * Created by anonymous
 */

enum OfferType {
    RENT("Pronájem"), SALE("Prodaz"), JOINT("Spolubydleni");

    private final String text;

    private OfferType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}


public class HouseItem implements Parcelable {
    public String Id;
    public String mAddress;
    // Sale or rent
    public OfferType mOfferType;
    // Sale or rent
    public String mPropertyType;
    // 2+1, etc
    public String mLayout;
    public String mOwnership;
    public String mEnergyType;
    public String mBuildingType;
    public String mEquipment;
    public String mDescription;
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
        this.mOfferType = OfferType.values()[item.getAsJsonObject().getAsJsonPrimitive("mOfferType1").getAsInt()];
        this.mPropertyType = item.getAsJsonObject().getAsJsonPrimitive("mPropertyType").getAsString();
        this.mLayout = item.getAsJsonObject().getAsJsonPrimitive("mLayout").getAsString();
        this.mOwnership = item.getAsJsonObject().getAsJsonPrimitive("mOwnership").getAsString();
        this.mEnergyType = item.getAsJsonObject().getAsJsonPrimitive("mEnergyType").getAsString();
        this.mBuildingType = item.getAsJsonObject().getAsJsonPrimitive("mBuildingType").getAsString();
        this.mEquipment = item.getAsJsonObject().getAsJsonPrimitive("mEquipment").getAsString();
        this.mDescription = item.getAsJsonObject().getAsJsonPrimitive("mDescription").getAsString();
        this.mSize = item.getAsJsonObject().getAsJsonPrimitive("mSize").getAsInt();
        this.mPrice = item.getAsJsonObject().getAsJsonPrimitive("mPrice").getAsInt();
        this.mFees = item.getAsJsonObject().getAsJsonPrimitive("mFees").getAsInt();
        this.mFloor = item.getAsJsonObject().getAsJsonPrimitive("mFloor").getAsInt();
        this.mZipCode = item.getAsJsonObject().getAsJsonPrimitive("mZipCode").getAsInt();
        this.mImgPreview = item.getAsJsonObject().getAsJsonPrimitive("mImgPreview").getAsString();
        this.mHouseInfo = item.getAsJsonObject().getAsJsonPrimitive("mHouseInfo").getAsString();
        this.mBalkony = item.getAsJsonObject().getAsJsonPrimitive("mBalkony").getAsBoolean();
        this.mTerrace = item.getAsJsonObject().getAsJsonPrimitive("mTerrace").getAsBoolean();
    }

    public HouseItem(Parcel in){
        this.Id = in.readString();
        this.mAddress = in.readString();
        this.mOfferType = OfferType.values()[in.readInt()];
        this.mPropertyType = in.readString();
        this.mLayout = in.readString();
        this.mOwnership = in.readString();
        this.mEnergyType = in.readString();
        this.mBuildingType = in.readString();
        this.mEquipment = in.readString();
        this.mDescription = in.readString();
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
        parcel.writeString(mPropertyType);
        parcel.writeString(mLayout);
        parcel.writeString(mOwnership);
        parcel.writeString(mEnergyType);
        parcel.writeString(mBuildingType);
        parcel.writeString(mEquipment);
        parcel.writeString(mDescription);
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