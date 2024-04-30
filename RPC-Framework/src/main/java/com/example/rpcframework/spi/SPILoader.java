package com.example.rpcframework.spi;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SPILoader {
    private String SYS_SPI_DIR = "META-INF/system";
    private String PERSONAL_SPI_DIR = "META-INF/personal";
    private String[] prefixs = {SYS_SPI_DIR, PERSONAL_SPI_DIR};
    // 每个元素为一个策略的某个具体实现类;HashMap线程不安全,需要使用ConcurrentHashMap
    private static final Map<String,Class> ImplClass_Map = new ConcurrentHashMap<>();
    // 每个元素为某个策略的全部实现类Map
    private static final Map<String,Map<String,Class>> StrategiesMap_Map = new ConcurrentHashMap<>();
    private static final Map<String,Object> Singletons_Map = new ConcurrentHashMap<>();

    private static class SPILoaderHolder {
        private static SPILoader INSTANCE = new SPILoader();
    }

    public static SPILoader getInstance() {
        return SPILoaderHolder.INSTANCE;
    }

    public <V> V get(String name) {
        if (!Singletons_Map.containsKey(name)) {
            try{
                Singletons_Map.put(name,ImplClass_Map.get(name).newInstance());
            }catch (IllegalAccessException e) {
                e.printStackTrace();
            }catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return (V) Singletons_Map.get(name);
    }

     public List<Object> getMap(Class clazz) throws ClassNotFoundException {
        String name = clazz.getName();
        if (!StrategiesMap_Map.containsKey(name)) {
            throw new ClassNotFoundException("Class "+name+" has not been loaded");
        }
        Map<String,Class> map = StrategiesMap_Map.get(name);
        List<Object> results = new ArrayList<>();
        map.forEach((k,v)->{
            try {
                results.add(Singletons_Map.getOrDefault(k,v.newInstance()));
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return results;
     }

     /**
      * 从/META-INF/personal和/META-INF/system中读取各策略具体实现类并加载*/
    public void loadClasses(Class<?> aclass) throws IOException, ClassNotFoundException {
        if (aclass == null) {
            throw new IllegalArgumentException("The class needs load settings is NULL");
        }
        ClassLoader classLoader = this.getClass().getClassLoader();
        Map<String,Class> tempMap = new HashMap<>();
        for (String path:prefixs) {
            path = path + aclass.getName();
            // ClassLoader.getResources 双亲委派模型
            Enumeration<URL> enumeration = classLoader.getResources(path);
            while (enumeration.hasMoreElements()) {
                // url:配置文件
                URL url = enumeration.nextElement();
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] arr = line.split("=");
                    if (arr[0]!=null && arr[1]!=null){
                        ImplClass_Map.put(arr[0],Class.forName(arr[1]));
                        tempMap.put(arr[0],Class.forName(arr[1]));
                        log.info("加载{}为:{}",arr[0],arr[1]);
                    }
                }
            }
        }
        StrategiesMap_Map.put(aclass.getName(), tempMap);
    }
}
