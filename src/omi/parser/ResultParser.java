package omi.parser;/* This class will be used as a Parser for parsing OMI results
Input -  Unstructured OMI Query Result
Output - 3 Cases
         a) Single Instance OMI Result: List WMI object properties (List Size = 1)
         b) Multiple Instances : ArrayList of List where each List representing WMI object .
                                  (List Size = depending upon the number of Instances in OMI Result)
         c) Single/Multiple Instances containing inherited Instances: ArrayList of List where each List representing WMI object.
                               Inherited Instances will have [parent key.inherited key] as its key.
                               (List Size = depending upon the number of Instances in OMI Result)

Microsoft_OMI_Result_Parser

  Sample output of OMI Query done to a Linux Box from Windows Machine.

   instance of Win32_OperatingSystem
   {
   BootDevice=\Device\HarddiskVolume1
   BuildNumber=11993
   BuiltType=MultiProcesor Free
   Caption=Microsoft Windows Server 2022
   CodeSet=2244
   CountryCode=4
   CreationClassName=Win32_OperatingSystem
   CSCreationClassName=Win32_ComputerSystem
   CSNAME=EC2AMAZ-9977AZ
   CurrentTimeZone=0
   FreePhysicalMemory=997732
   FreeVirtualMemory=1933542
   InstallDate= instance of InstallDate
   {
   Datetime=2022-07-08T21:31:38Z
   }
   LastBootUpTime= instance of LastBootUpTime
   {
   Datetime=2022-07-08T21:31:38Z
   }
   LocalDateTime= instance of LocalDateTime
   {
   LocalDateTime=2022-07-08T21:31:38Z
   }
   Locale=0409
   Manufacturer= Microsoft Corporation
   OSArchitecture=64 bit
   OSProductType=500
   Primary=true
   ServicePackMajorVersion=6
   Status=OK
   Organization = Amazon.com
   OSProductSuite=000
   OSType=19
   RegisteredUser=EC2
   PortableOperatingSystem=false
   MUILanguage=en-US
   }

*/

import java.util.*;

public class ResultParser {


    String QUERY_RESULTS = Constants.result;
    String OPEN_BRACKET = Constants.OPEN_PAR;
    String CLOSED_BRACKET = Constants.CLOSED_PAR;
    String INSTANCE_OF = Constants.INSTANCE_OFF;
    String EQUALS_OP = Constants.EQUALS_OP;
    String BLANK_ST = Constants.BLANK_STRING;


    public List<Map<String,String>> parseOmiResult(Map<String, String > output)
    {
        Stack<String> keyAndValue = new Stack<>();
        Stack<String> braces_Identification = new Stack<>();
        Stack<String> omi_instance = new Stack<>();
        Scanner sc = new Scanner(output.get(QUERY_RESULTS));
        List<Map<String, String>> resultantValues = new ArrayList<>();
        int indicator = 0;
        while(sc.hasNextLine()){
            String inputLine = sc.nextLine().trim();
            if(inputLine.equalsIgnoreCase(OPEN_BRACKET)){
                if(braces_Identification.size() >= 1){
                    indicator = 1;
                }
                braces_Identification.push(inputLine);
            }else if (inputLine.equalsIgnoreCase(CLOSED_BRACKET)){
                int sizeOfBraces = braces_Identification.size();
                if(OPEN_BRACKET.equalsIgnoreCase(braces_Identification.peek()) && sizeOfBraces == 1){
                braces_Identification.pop();
                Map<String, String> tempValue = new HashMap<>();
                while(!keyAndValue.isEmpty()){
                    String linex = keyAndValue.pop();
                    String[] keyValuex = linex.split(EQUALS_OP);
                    if(keyValuex.length == 2) {
                        tempValue.put(keyValuex[0].trim().toUpperCase(), keyValuex[1].trim());

                    }else {
                        tempValue.put(keyValuex[0].trim().toUpperCase(), BLANK_ST);
                    }
                }
                resultantValues.add(tempValue);
                }else if (OPEN_BRACKET.equalsIgnoreCase(braces_Identification.peek()) && sizeOfBraces > 1 &&  !omi_instance.isEmpty()){
                    braces_Identification.pop();
                    omi_instance.pop();
                    indicator =0;
                }

        }else if(inputLine.equalsIgnoreCase(EQUALS_OP) && !inputLine.contains(INSTANCE_OF)){
                if(indicator == 0){
                    String[] tempKeyValue = inputLine.split(EQUALS_OP);
                    if(tempKeyValue.length == 2){
                        String objectValueString = tempKeyValue[1];
                        if(objectValueString.startsWith(OPEN_BRACKET) && objectValueString.endsWith(CLOSED_BRACKET)){
                            objectValueString = objectValueString.substring(1, objectValueString.length()-1);
                            StringBuilder pair = new StringBuilder(tempKeyValue[0]);
                            pair.append(EQUALS_OP).append(objectValueString);
                        }
                        else {
                            keyAndValue.push(inputLine);
                        }
                    }
                    else{
                        keyAndValue.push(inputLine);
                    }
                }
                if(indicator == 1 && !omi_instance.isEmpty()){
                    String prefixKey = omi_instance.peek();
                    String[] tempKeyValue = inputLine.split(EQUALS_OP);
                    StringBuilder keyValueWmiPair = new StringBuilder(prefixKey);
                    if(tempKeyValue.length == 2){
                        String valueString = tempKeyValue[1];
                        if(valueString.startsWith(OPEN_BRACKET) && valueString.endsWith(CLOSED_BRACKET)){
                            valueString = valueString.substring(1, valueString.length()-1);
                            keyValueWmiPair.append(EQUALS_OP).append(valueString);
                        }else{
                            keyValueWmiPair.append(EQUALS_OP).append(tempKeyValue[1].trim());
                        }
                    }else {
                        keyValueWmiPair.append(EQUALS_OP).append(BLANK_ST);
                    }
                    keyAndValue.push(keyValueWmiPair.toString());
                }
            }else if(inputLine.contains(EQUALS_OP) && inputLine.contains(INSTANCE_OF)){
                String[] instances = inputLine.split(EQUALS_OP);
                omi_instance.push(instances[0]);
            }
        }
        sc.close();
        return resultantValues;
    }

}
