package com.idevicesinc.sweetblue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;

import com.idevicesinc.sweetblue.BleManagerConfig.AdvertisingFilter;
import com.idevicesinc.sweetblue.BleManagerConfig.AdvertisingFilter.Ack;
import com.idevicesinc.sweetblue.BleManagerConfig.AdvertisingFilter.Packet;
import com.idevicesinc.sweetblue.utils.State;

/**
 * 
 * 
 *
 */
class P_AdvertisingFilterManager
{
	private final ArrayList<BleManagerConfig.AdvertisingFilter> m_filters = new ArrayList<BleManagerConfig.AdvertisingFilter>();
	private final AdvertisingFilter m_default;
	
	P_AdvertisingFilterManager(AdvertisingFilter defaultFilter)
	{
		m_default = defaultFilter;
	}
	
	void clear()
	{
		m_filters.clear();
	}
	
	void remove(AdvertisingFilter filter)
	{
		while( m_filters.remove(filter) ){};
	}
	
	void add(AdvertisingFilter filter)
	{
		if( filter == null )  return;
		
		if( m_filters.contains(filter) )
		{
			return;
		}
		
		m_filters.add(filter);
	}
	
	BleManagerConfig.AdvertisingFilter.Ack allow(BluetoothDevice nativeInstance, List<UUID> uuids, String deviceName, String normalizedDeviceName, byte[] scanRecord, int rssi, State.ChangeIntent lastDisconnectIntent)
	{
		if( m_filters.size() == 0 && m_default == null )  return Ack.yes();
		
		Packet packet = null;
		
		if( m_default != null )
		{
			packet = new Packet(nativeInstance, uuids, deviceName, normalizedDeviceName, scanRecord, rssi, lastDisconnectIntent);
			
			Ack ack = m_default.acknowledgeDiscovery(packet);
			
			if( ack.ack() )
			{
				return ack;
			}
		}
		
		for( int i = 0; i < m_filters.size(); i++ )
		{
			packet = packet != null ? packet : new Packet(nativeInstance, uuids, deviceName, normalizedDeviceName, scanRecord, rssi, lastDisconnectIntent);
			
			AdvertisingFilter ithFilter = m_filters.get(i);
			
			Ack ack = ithFilter.acknowledgeDiscovery(packet);
			
			if( ack.ack() )
			{
				return ack;
			}
		}
		
		return BleManagerConfig.AdvertisingFilter.Ack.no();
	}
}
