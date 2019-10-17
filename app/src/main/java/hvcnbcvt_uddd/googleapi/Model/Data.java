package hvcnbcvt_uddd.googleapi.Model;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("entity")
    private Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
