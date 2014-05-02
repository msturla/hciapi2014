package ar.edu.itba.hciapi;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ar.edu.itba.hciapi.api.ApiCallback;
import ar.edu.itba.hciapi.api.Api;
import ar.edu.itba.hciapi.model.ProductAttribute;
import ar.edu.itba.hciapi.model.SignInResult;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Api.get().getAttributes(new ApiCallback<List<ProductAttribute>>() {

        			@Override
        			public void call(List<ProductAttribute> result, Exception exception) {
        				String msg = "";
        				if (exception != null) {
        					msg = exception.getMessage();
        				} else {
        					
        					for (ProductAttribute attr : result) {
        						msg += attr.getName() + ", ";
        					}
        				}
        				Toast.makeText(MainActivity.this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        				
        			}
        			
        		});
            }
        });
        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Api.get().getAttributeById(7, new ApiCallback<ProductAttribute>() {

        			@Override
        			public void call(ProductAttribute result, Exception exception) {
        				String msg = "";
        				if (exception != null) {
        					msg = exception.getMessage();
        				} else {
        					msg += "name: " + result.getName();
        					msg += "values: ";
        					for (String val : result.getValues()) {
        						msg += val + ", ";
        					}
        				}
        				Toast.makeText(MainActivity.this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        				
        			}
        			
        		});
            }
        });
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Api.get().signIn("apitestuser", "apitestuser2", new ApiCallback<SignInResult>() {

        			@Override
        			public void call(SignInResult result, Exception exception) {
        				String msg = "";
        				if (exception != null) {
        					msg = exception.getMessage();
        				} else {
        					msg += "token: " + result.getToken();
        					msg += " username: " + result.getAccount().getUsername();
        				}
        				Toast.makeText(MainActivity.this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        			}
        		});
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
