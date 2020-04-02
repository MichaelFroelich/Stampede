package org.stampede.youngling;

import java.nio.file.Path;
import java.util.Map.Entry;

import org.stampede.Stampede;
import org.stampede.StampedeConfig;
import org.stampede.Util;
import org.stampede.config.Config;
import org.stampede.config.ConfigMediator;
import org.stampede.model.Role;

public interface IRoar {

	/**
	 * publishes all the configs onto the zookeeper database
	 * @param stampede
	 * @param configDetails
	 * @throws Exception
	 */
	default void roar(Stampede stampede, ConfigMediator configDetails) throws Exception {
		boolean SERIALISE_WHOLE_TREE = StampedeConfig.shouldSerializeWholeTree();
		for(Entry<String, Object> pair : configDetails.flatten().entrySet()) {
			Object result = ((Config) pair.getValue()).getResult();
			if(SERIALISE_WHOLE_TREE || result instanceof Path) {
				String key = pair.getKey();
				String extension = Util.getFinalLabel(((Path)result).toString());
				if(result instanceof Path && !extension.isEmpty()) {
					key += "." + extension;
				}
				stampede.createZookeeperStore(key, result);
			}
		}
	}
}
