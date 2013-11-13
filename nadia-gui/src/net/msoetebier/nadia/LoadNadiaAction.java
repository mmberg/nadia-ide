package net.msoetebier.nadia;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.msoetebier.nadia.view.NavigationView;

import org.eclipse.jface.action.Action;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.ui.IWorkbenchWindow;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.entity.mime.content.*;
import org.apache.http.entity.mime.*;

@SuppressWarnings("deprecation")
public class LoadNadiaAction extends Action{
	private static final long serialVersionUID = -9188315653489303600L;
	private final IWorkbenchWindow window;

	public LoadNadiaAction(IWorkbenchWindow window) {
		super("Load XML File in NADIA");
		setId(this.getClass().getName());
		this.window = window;
	}
	
	public void run() {
		if(window != null) {
			postIt();
		}
	}
	
	private void postIt(){
		try {
			HttpClient httpClient = new DefaultHttpClient();
			SSLContext sslContext = SSLContext.getInstance("SSL");
		    // set up a TrustManager that trusts everything
		    sslContext.init(null,
		    		new TrustManager[] { new X509TrustManager() {
		    			public X509Certificate[] getAcceptedIssuers() {
		    				System.out.println("getAcceptedIssuers =============");
		                    return null;
		                }

		                public void checkClientTrusted(
		                	X509Certificate[] certs, String authType) {
		                	System.out.println("checkClientTrusted =============");
		                }

		                public void checkServerTrusted(
		                	X509Certificate[] certs, String authType) {
		                	System.out.println("checkServerTrusted =============");
		                }
		    		} }, new SecureRandom());
		        
		    SSLSocketFactory ssf = new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		    ClientConnectionManager ccm = httpClient.getConnectionManager();
		    SchemeRegistry sr = ccm.getSchemeRegistry();
		    sr.register(new Scheme("https", 443, ssf));            
		    
			
			NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
			HttpPost httppost = new HttpPost(navigationView.getNadiaUrlPath());
			String fileName = navigationView.getXmlPath();
//			String fileName="D:/dummy2.xml"; 
	
			FileBody bin = new FileBody(new File(fileName));
			StringBody comment=null;
			comment = new StringBody("Filename: " + fileName);

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("dialogxml", bin);
			reqEntity.addPart("comment", comment);
			httppost.setEntity(reqEntity);

			HttpResponse response = httpClient.execute(httppost);
			Header[] location = response.getHeaders("Location");
			String url = location[0].getValue();
			
			HttpEntity resEntity = response.getEntity();
			System.out.println("res"+resEntity.toString());
			
			UrlLauncher launcher = RWT.getClient().getService(UrlLauncher.class);
			launcher.openURL(url);
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
	}
}