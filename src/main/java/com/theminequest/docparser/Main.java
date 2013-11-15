package com.theminequest.docparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theminequest.doc.V1Documentation;

// ty to http://stackoverflow.com/a/60766
public class Main extends JavaPlugin {
	
	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			
			@Override
			public void run() {
				Reflections reflections = new Reflections(ClasspathHelper.forClassLoader(), new TypeAnnotationsScanner(), new SubTypesScanner());
				Set<Class<?>> classes = reflections.getTypesAnnotatedWith(V1Documentation.class);
				
				HashMap<String, HashMap<String, HashMap<String, Object>>> information = new HashMap<>();
				Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
				
				for (Class<?> clazz : classes) {
					V1Documentation doc = clazz.getAnnotation(V1Documentation.class);
					if (!information.containsKey(doc.type()))
						information.put(doc.type(), new HashMap<String, HashMap<String, Object>>());
					
					HashMap<String, HashMap<String, Object>> type = information.get(doc.type());
					HashMap<String, Object> props = new HashMap<String, Object>();
					props.put("description", doc.description());
					props.put("arguments", doc.arguments());
					props.put("typearguments", doc.typeArguments());
					props.put("hidden", Boolean.toString(doc.hide()));
					type.put(doc.ident(), props);
				}
				
				File f = new File("result");
				
				try {
					if (!f.exists())
						f.createNewFile();
					try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)))) {
						writer.println(gson.toJson(information));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
			
		}, 5);
	}
}
