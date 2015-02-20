/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.alts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.swing.JOptionPane;

import net.minecraft.client.Minecraft;
import tk.wurst_client.utils.MiscUtils;

public class Encryption
{
	private static KeyPair key;
	private static File private_file = System.getProperty("user.home") != null ? new File(System.getProperty("user.home") + "\\.ssh\\wurst_rsa") : null;
	private static File public_file = System.getProperty("user.home") != null ? new File(System.getProperty("user.home") + "\\.ssh\\wurst_rsa.pub") : null;

	public static String encrypt(String string)
	{
		checkKey();
		return string;
	}

	public static String decrypt(String string)
	{
		checkKey();
		return string;
	}
	
	public static void generateKey()
	{
		try
		{
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(1024, new SecureRandom());
			key = keygen.generateKeyPair();
			if(private_file == null || public_file == null)
			{
				JOptionPane.showMessageDialog(null, "Cannot create RSA key.\nThis is a bug, please report it!", "Error", JOptionPane.ERROR_MESSAGE);
				MiscUtils.openLink("https://github.com/Wurst-Imperium/Wurst-Client/issues?q=cannot+create+RSA+key");
				Minecraft.getMinecraft().shutdown();
				return;
			}
			if(!private_file.exists())
			{
				if(!private_file.getParentFile().exists())
					private_file.getParentFile().mkdirs();
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(private_file));
				save.writeObject(key.getPrivate().getEncoded());
				save.close();
			}
			if(!public_file.exists())
			{
				if(!public_file.getParentFile().exists())
					public_file.getParentFile().mkdirs();
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(public_file));
				save.writeObject(key.getPublic().getEncoded());
				save.close();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void loadKey()
	{
		try
		{
			if(private_file == null || public_file == null)
			{
				JOptionPane.showMessageDialog(null, "Cannot load RSA key.\nThis is a bug, please report it!", "Error", JOptionPane.ERROR_MESSAGE);
				MiscUtils.openLink("https://github.com/Wurst-Imperium/Wurst-Client/issues?q=cannot+load+RSA+key");
				Minecraft.getMinecraft().shutdown();
			}
			if(hasKey())
				generateKey();
			else
			{
				ObjectInputStream loadPrivate = new ObjectInputStream(new FileInputStream(private_file));
				ObjectInputStream loadPublic = new ObjectInputStream(new FileInputStream(public_file));
				final byte[] privateKey = (byte[])loadPrivate.readObject();
				final byte[] publicKey = (byte[])loadPublic.readObject();
				loadPrivate.close();
				loadPublic.close();
				key = new KeyPair(new PublicKey()
				{
					@Override
					public String getFormat()
					{
						return null;
					}
					
					@Override
					public byte[] getEncoded()
					{
						return publicKey;
					}
					
					@Override
					public String getAlgorithm()
					{
						return "RSA";
					}
				}, new PrivateKey()
				{
					@Override
					public String getFormat()
					{
						return null;
					}
					
					@Override
					public byte[] getEncoded()
					{
						return privateKey;
					}
					
					@Override
					public String getAlgorithm()
					{
						return "RSA";
					}
				});
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean hasKey()
	{
		return private_file != null
			&& private_file.exists()
			&& public_file != null
			&& public_file.exists();
	}
	
	private static void checkKey()
	{
		if(!hasKey())
			generateKey();
		else
			loadKey();
	}
}
