package eu.bioimage.celltools.general.datamanaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.bioimage.celltools.cluster3d.model.C3D;

public class ClassNameMappingObjectInputStream extends ObjectInputStream {
	 public static Map<String, Class> classNameMapping = initclassNameMapping(); 

	    private static Map<String, Class> initclassNameMapping(){
	        Map<String, Class> res = new HashMap<String, Class>();
	        res.put("eljarasok.C3D", eu.bioimage.celltools.cluster3d.model.C3D.class);
	        res.put("eljarasok.SerializableRoi", eu.bioimage.celltools.sproutanalyzing.model.SerializableRoi.class);
	        res.put("eljarasok.SPR", eu.bioimage.celltools.sproutanalyzing.model.SPR.class);
	        return Collections.unmodifiableMap(res);
	    }

	    public ClassNameMappingObjectInputStream(InputStream in) throws IOException {
	        super(in);
	    }


	    protected ClassNameMappingObjectInputStream() throws IOException, SecurityException {
	        super();
	    }

	    @Override
	    protected java.io.ObjectStreamClass readClassDescriptor() 
	            throws IOException, ClassNotFoundException {
	        ObjectStreamClass desc = super.readClassDescriptor();
	        if (classNameMapping.containsKey(desc.getName())){
	            return ObjectStreamClass.lookup(classNameMapping.get(desc.getName()));
	        }
	        return desc;
	    }

}
