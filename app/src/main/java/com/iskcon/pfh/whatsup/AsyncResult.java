package com.iskcon.pfh.whatsup;

/**
 * Created by i308830 on 5/19/17.
 */

import org.json.JSONObject;

public interface AsyncResult {
    void onResult(JSONObject object);
}
