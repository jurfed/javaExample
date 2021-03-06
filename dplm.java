public class SendReestr {
    private CloseableHttpClient httpclient;
    private HttpPost httppost;
    private MultipartEntityBuilder builder;
    private FileBody fileBody;
    private HttpResponse response;
    private String responseString;
    private HttpEntity resEntity;
    private String address, path, regnum;
    private int buyRegNumber,payRegNumber;
    private String result;
    boolean b;

    
    /**
     * @param args the command line arguments
     */
    //адрес, путь к реестрам,рег номер оператора, номер на покупку и на оплату
SendReestr(String param, String param0, String param1, int buyRegNumber1, int payRegNumber1) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {      
System.out.println("before httpclient");
httpclient = HttpClients.custom().
setHostnameVerifier(new AllowAllHostnameVerifier()).
setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
{
public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                        {
                            return true;
                        }
                    }).build()).build();
address=param;
path=param0;
regnum=param1;
buyRegNumber=buyRegNumber1;
payRegNumber=payRegNumber1;      
System.out.println("after httpclient");
}

int sendBuy() throws IOException, InterruptedException{//послать реестр на покупку
        try {
                System.out.println("before send 1");
        httppost = new HttpPost(address+"/cp_payments/buy_check/");
        builder = MultipartEntityBuilder.create();       
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("clientId", regnum);
        builder.addTextBody("requestNum",   Integer.toString(buyRegNumber));
        fileBody = new FileBody(new File(path+"\\reestrs\\test1.csv.gz"));
        builder.addPart("file", fileBody);
        httppost.setEntity(builder.build());
        response = httpclient.execute(httppost);//отправка запроса
        resEntity = response.getEntity();
        responseString = EntityUtils.toString(resEntity, "UTF-8");
        System.out.println(responseString);//вывод результата  
        System.out.println("after send 2");
        b = Pattern.matches(".*\"state\":1.*", responseString);
    } finally {
        //try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
    }
        if(b) return 0;
        else return 1;
    }
    
        int sendPay() throws IOException, InterruptedException{  
                   try {
        httppost = new HttpPost(address+"/cp_payments/pay_check/");
        builder = MultipartEntityBuilder.create();       
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("clientId", regnum);
        builder.addTextBody("requestNum",   Integer.toString(payRegNumber));
        fileBody = new FileBody(new File(path+"\\reestrs\\test2.csv.gz"));
        builder.addPart("file", fileBody);
        httppost.setEntity(builder.build());
        response = httpclient.execute(httppost);//отправка запроса
        resEntity = response.getEntity();
        responseString = EntityUtils.toString(resEntity, "UTF-8");
        b = Pattern.matches(".*\"state\":1.*", responseString);
    } finally {
        try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
    }
        if(b) return 0;
        else return 1;
    }  
}
