package org.eclipse.kura.DHT11;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.message.KuraPayload;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DHT11Sensor implements ConfigurableComponent, CloudClientListener {

	private CloudService m_cloudService;
	private CloudClient m_cloudClient;
	private ScheduledExecutorService m_worker;
	private ScheduledFuture<?> m_handle;
	DHT11 dht11;
	String result;
	double temp;
	double humi;

	private static final Logger s_logger = LoggerFactory.getLogger(DHT11Sensor.class);
	private static final String APP_ID = "DHT11Sensor";

	public DHT11Sensor() {
		super();
		m_worker = Executors.newSingleThreadScheduledExecutor();
	}

	public void setCloudService(CloudService cloudService) {
		m_cloudService = cloudService;
	}

	public void unsetCloudService(CloudService cloudService) {
		m_cloudService = null;
	}

	protected void activate(ComponentContext componentContext, Map<String, Object> properties) {

		// m_properties = properties;
		/*
		 * for (String s : properties.keySet()) { s_logger.info("Activate - " + s + ": "
		 * + properties.get(s)); }
		 */

		// get the mqtt client for this application
		try {

			// Acquire a Cloud Application Client for this Application
			s_logger.info("Getting CloudClient for {}...", APP_ID);
			m_cloudClient = m_cloudService.newCloudClient(APP_ID);
			m_cloudClient.addCloudClientListener(this);

			// Don't subscribe because these are handled by the default
			// subscriptions and we don't want to get messages twice
			dht11 = new DHT11();
			doUpdate(false);
		} catch (Exception e) {
			s_logger.error("Error during component activation", e);
			throw new ComponentException(e);
		}

		s_logger.info("Bundle " + APP_ID + " has started!");

	}

	protected void deactivate(ComponentContext componentContext) {
		m_worker.shutdown();

		// Releasing the CloudApplicationClient
		s_logger.info("Releasing CloudApplicationClient for {}...", APP_ID);
		m_cloudClient.release();

		s_logger.debug("Deactivating DHT11... Done.");
		s_logger.info("Bundle " + APP_ID + " has stopped!");

	}

	public void updated(Map<String, Object> properties) {
		s_logger.info("Updated DHT11...");

		// store the properties received
		/*
		 * m_properties = properties; for (String s : properties.keySet()) {
		 * s_logger.info("Update - " + s + ": " + properties.get(s)); }
		 */
		// try to kick off a new job
		dht11 = new DHT11();
		doUpdate(true);
		s_logger.info("Updated DHT11... Done.");
	}

	private void doUpdate(boolean onUpdate) {
		// cancel a current worker handle if one if active
		if (m_handle != null) {
			m_handle.cancel(true);
		}

		// schedule a new worker based on the properties of the service
		int pubrate = 2;
		m_handle = m_worker.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName(getClass().getSimpleName());
				doPublish();
			}
		}, 0, pubrate, TimeUnit.SECONDS);
	}

	private void doPublish() {
		// fetch the publishing configuration from the publishing properties
		String topic = "DHT11TechTalk";
		Integer qos = 0;
		Boolean retain = false;

		// Allocate a new payload
		KuraPayload payload = new KuraPayload();

		// Timestamp the message
		payload.setTimestamp(new Date());

		dht11.readTempAndHumi();
		temp = dht11.getTemperature();
		humi = dht11.getHumidity();

		if ((temp != Double.MAX_VALUE) && (humi != Double.MAX_VALUE)) {
			// Add the temperature and humidity as a metric to the payload

			payload.addMetric("TemperatureReading", temp);
			payload.addMetric("HumidityReading", humi);

			// Publish the message
			try {
				m_cloudClient.publish(topic, payload, qos, retain);
				s_logger.info("--------------------------" + "\n");
				s_logger.info(
						"Published on topic: " + topic + "\n" + " Temperature is: " + temp + " & Humidity is: " + humi);
				s_logger.info("--------------------------" + "\n");
				Thread.sleep(4000);
			} catch (Exception e) {
				s_logger.error("Cannot publish topic: ", e);
			}
		}
	}

	@Override
	public void onConnectionEstablished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionLost() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onControlMessageArrived(String arg0, String arg1, KuraPayload arg2, int arg3, boolean arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageArrived(String arg0, String arg1, KuraPayload arg2, int arg3, boolean arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessageConfirmed(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessagePublished(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}
}
