package com.affinity.sk_partner;

import com.affinity.sk_partner.pojoClasses.BaseResponse;
import com.affinity.sk_partner.pojoClasses.Users;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetails extends BaseResponse {

    private String BRANCH, BANK;

    public String getBRANCH() {
        return BRANCH;
    }

    public void setBRANCH(String BRANCH) {
        this.BRANCH = BRANCH;
    }

    public String getBANK() {
        return BANK;
    }

    public void setBANK(String BANK) {
        this.BANK = BANK;
    }

    /*@SerializedName("response")
    @Expose
    private BranchDetails response;

    public BranchDetails getResponse() {
        return response;
    }

    public void setResponse(BranchDetails response) {
        this.response = response;
    }

    public class BranchDetails{

        @SerializedName("BRANCH")
        @Expose
        private String BRANCH;

        @SerializedName("BANK")
        @Expose
        private String BANK;

        @SerializedName("ADDRESS")
        @Expose
        private String ADDRESS;

        public String getBRANCH() {
            return BRANCH;
        }

        public void setBRANCH(String BRANCH) {
            this.BRANCH = BRANCH;
        }

        public String getBANK() {
            return BANK;
        }

        public void setBANK(String BANK) {
            this.BANK = BANK;
        }

        public String getADDRESS() {
            return ADDRESS;
        }

        public void setADDRESS(String ADDRESS) {
            this.ADDRESS = ADDRESS;
        }
    }*/
}
