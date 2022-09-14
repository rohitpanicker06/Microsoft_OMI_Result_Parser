package omi.parser;

import java.util.List;
import java.util.Map;

public interface ParsingGatewayInterface {

      Map<String,String> singleInstance(Map<String, String> output);
     List<Map<String,String>> multipleInstance(Map<String,String> output);
     List<Map<String,String>> multiLevelInstance(Map<String,String> output);





}
