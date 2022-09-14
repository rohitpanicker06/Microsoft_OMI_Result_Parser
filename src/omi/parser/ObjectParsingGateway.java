package omi.parser;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

public class ObjectParsingGateway implements ParsingGatewayInterface {
    private ResultParser resultParser = new ResultParser();

    @Override
    public  Map<String,String> singleInstance(Map<String,String> output){
        return resultParser.parseOmiResult(output).get(0);
    }

    @Override
    public List<Map<String,String>> multipleInstance(Map<String,String> output){
        return resultParser.parseOmiResult(output);
    }

    @Override
    public List<Map<String,String>> multiLevelInstance(Map<String,String> output){
        return resultParser.parseOmiResult(output);
    }

}
