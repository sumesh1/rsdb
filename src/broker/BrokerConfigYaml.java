package broker;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import broker.group.ExternalGroupConfig;
import pointdb.base.PointdbConfig;
import util.Util;
import util.yaml.YamlMap;

public class BrokerConfigYaml extends BrokerConfig {
	private static final Logger log = LogManager.getLogger();

	public BrokerConfigYaml(String filename) {
		try {
			InputStream in = new FileInputStream(new File(filename));
			YamlMap configMap = YamlMap.ofObject(new Yaml().load(in));

			for(YamlMap pc:configMap.optList("pointdb").asMaps()) {
				PointdbConfig pointdbConfig = PointdbConfig.ofYAML(pc);
				if(Util.isValidIdentifier(pointdbConfig.name)) {
					pointdbMap.put(pointdbConfig.name, pointdbConfig);
				} else {
					log.warn("pointdb not inserted: invalid identifier: "+pointdbConfig.name+" in "+filename);
				}
			}

			for(YamlMap pc:configMap.optList("Points_of_interest").asMaps()) {
				ExternalGroupConfig poiConfig = ExternalGroupConfig.ofYAML(pc);
				if(Util.isValidIdentifier(poiConfig.name)) {
					poiGroupMap.put(poiConfig.name, poiConfig);
				} else {
					log.warn("poiGroup not inserted: invalid identifier: "+poiConfig.name+" in "+filename);
				}
			}

			for(YamlMap pc:configMap.optList("Regions_of_interest").asMaps()) {
				ExternalGroupConfig roiConfig = ExternalGroupConfig.ofYAML(pc);
				if(Util.isValidIdentifier(roiConfig.name)) {
					roiGroupMap.put(roiConfig.name, roiConfig);
				} else {
					log.warn("roiGroup not inserted: invalid identifier: "+roiConfig.name+" in "+filename);
				}
			}

			try {
				for(YamlMap yamlMap:configMap.optList("jws").asMaps()) {
					try {
						JwsConfig jwsConfig = JwsConfig.ofYAML(yamlMap);
						jwsConfigs.add(jwsConfig);
					} catch (Exception e) {
						log.warn(e);
					}
				}
			} catch (Exception e) {
				log.warn(e);
			}

			this.serverConfig = configMap.funMap("server", m->ServerConfig.ofYAML(m), ServerConfig::new);
		}catch (Exception e) {
			e.printStackTrace();
			log.error("config YAML file error in "+filename+"  "+e);
			//throw new RuntimeException(e);
		}
	}
}
