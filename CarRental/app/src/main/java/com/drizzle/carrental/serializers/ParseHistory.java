package com.drizzle.carrental.serializers;

import org.json.JSONObject;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseHistory implements Serializable {

    int id;

    String type;

    JSONObject content;
}
