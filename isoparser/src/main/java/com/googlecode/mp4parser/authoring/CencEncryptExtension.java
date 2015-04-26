package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;

import java.util.*;

/**
 *
 */
public class CencEncryptExtension implements SampleGroupExtension<CencSampleEncryptionInformationGroupEntry> {
    Map<CencSampleEncryptionInformationGroupEntry, long[]> cencSampleEncryptionGroups = new HashMap<CencSampleEncryptionInformationGroupEntry, long[]>();
    long[] samplesInGroup;
    UUID defaultKeyId;
    boolean subSampleEncryption;
    String encryptionAlgo = "cenc";
    boolean dummyIvs = false;
    boolean encryptButAllClear = false;

    List<CencSampleAuxiliaryDataFormat> cencSampleAuxiliaryDataFormats = new ArrayList<CencSampleAuxiliaryDataFormat>();

    public Map<CencSampleEncryptionInformationGroupEntry, long[]> getGroupEntries() {
        return Collections.unmodifiableMap(cencSampleEncryptionGroups);
    }


    public List<CencSampleAuxiliaryDataFormat> getCencSampleAuxiliaryDataFormats() {
        return cencSampleAuxiliaryDataFormats;
    }

    public void setCencSampleAuxiliaryDataFormats(List<CencSampleAuxiliaryDataFormat> cencSampleAuxiliaryDataFormats) {
        this.cencSampleAuxiliaryDataFormats = cencSampleAuxiliaryDataFormats;
    }

    public boolean isSubSampleEncryption() {
        return subSampleEncryption;
    }

    public void setSubSampleEncryption(boolean subSampleEncryption) {
        this.subSampleEncryption = subSampleEncryption;
    }

    public UUID getDefaultKeyId() {
        return defaultKeyId;
    }

    public void setDefaultKeyId(UUID defaultKeyId) {
        this.defaultKeyId = defaultKeyId;
    }

    public String getEncryptionAlgo() {
        return encryptionAlgo;
    }

    public void setEncryptionAlgo(String encryptionAlgo) {
        this.encryptionAlgo = encryptionAlgo;
    }

    public boolean isDummyIvs() {
        return dummyIvs;
    }
    /*
    * @param encryptionAlgo cenc or cbc1 (don't use cbc1)
            * @param dummyIvs disables RNG for IVs and use IVs starting with 0x00...000
            * @param encryptButAllClear will cause sub sample encryption format to keep full sample in clear (clear/encrypted pair will be len(sample)/0
*/
    public void setDummyIvs(boolean dummyIvs) {
        this.dummyIvs = dummyIvs;
    }

    public boolean isEncryptButAllClear() {
        return encryptButAllClear;
    }

    public void setEncryptButAllClear(boolean encryptButAllClear) {
        this.encryptButAllClear = encryptButAllClear;
    }
}
