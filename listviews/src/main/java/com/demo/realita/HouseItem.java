package com.demo.realita;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by anonymous
 */
public class HouseItem implements Parcelable {

    public String Id;
    public String mAddress;
    // Sale or rent
    public String mOfferType;
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

    public HouseItem(String Id, String Address, String OfferType, String PropertyType, String Layout
            , String Ownership, String EnergyType, String BuildingType, String Equipment
            , String Description, double Size, int Price, int Fees, int Floor, int ZipCode
            , String ImgPreview, String HouseInfo, boolean Balkony, boolean Terrace) {
        this.Id = Id;
        this.mAddress = Address;
        this.mOfferType = OfferType;
        this.mPropertyType = PropertyType;
        this.mLayout = Layout;
        this.mOwnership = Ownership;
        this.mEnergyType = EnergyType;
        this.mBuildingType = BuildingType;
        this.mEquipment = Equipment;
        this.mDescription = Description;
        this.mSize = Size;
        this.mPrice = Price;
        this.mFees = Fees;
        this.mFloor = Floor;
        this.mZipCode = ZipCode;
        this.mImgPreview = ImgPreview;
        this.mHouseInfo = HouseInfo;
        this.mBalkony = Balkony;
        this.mTerrace = Terrace;
    }

    public HouseItem(Parcel in){
        this.Id = in.readString();
        this.mAddress = in.readString();
        this.mOfferType = in.readString();
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
        parcel.writeString(mOfferType);
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