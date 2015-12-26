package com.amazon.movies.project;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * This class shows how to make a simple authenticated call to the
 * Amazon Product Advertising API.
 *
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ProductSearch {

    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "AKIAIS6HZ576JP2DQVRA";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "KxOkDMA1V+CFfnqCnVisQuHekyEQ2EM7SvqWlQiM";

    /*
     * Use the end-point according to the region you are interested in.
     */
    private static final String ENDPOINT = "webservices.amazon.com";
    SignedRequestHelper helper;
    public ProductSearch(){
        /*
         * Set up the signed requests helper.
         */
        try {
            helper = SignedRequestHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Node getMovieDetails(String prodids) throws IOException, SAXException, ParserConfigurationException, ParseException, InterruptedException {

    
        String requestUrl = null;

        Map<String, String> params = new HashMap<String, String>();

        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemLookup");
        params.put("AWSAccessKeyId", "AKIAIS6HZ576JP2DQVRA");
        params.put("AssociateTag", "mrpr0a-20");
        params.put("ItemId", prodids);
        params.put("IdType", "ASIN");
        params.put("ResponseGroup", "ItemAttributes");

        requestUrl = helper.sign(params);

//        System.out.println("Signed URL: \"" + requestUrl + "\"");
        
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = null;
        boolean notSuccess=true;
        while(notSuccess){
        	try{
        		 doc = db.parse(new URL(requestUrl).openStream());
        		 notSuccess=false;
        		 break;
        	}
        	catch(Exception e){
        		System.out.println(e);
        	   
        	}
        }
        
        NodeList node=null;
        try{
         node = doc.getElementsByTagName("Title");
        }
        catch(Exception e){
        	
        }
        return node.item(0);
    }
}


