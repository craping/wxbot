package client.pojo.disruptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

public class RecordHandler implements EventHandler<RecordEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(RecordHandler.class);
	
	private final long ordinal;
	
    private final long numberOfConsumers;
	
	public RecordHandler(final long ordinal, final long numberOfConsumers)  {
        this.ordinal = ordinal;
        this.numberOfConsumers = numberOfConsumers;
    }
	
	@Override
	public void onEvent(RecordEvent event, long sequence, boolean endOfBatch) throws Exception {
		if (event.getHash() % numberOfConsumers == ordinal){
			try {
				File file = new File(event.getPath());
				
				FileOutputStream fos = null;
				OutputStreamWriter osw = null;
				
				if (!file.exists())
					file.mkdirs();

				fos = new FileOutputStream(new File(event.getPath() + File.separator + event.getFileName()), true);
				osw = new OutputStreamWriter(fos, "UTF-8");
				osw.write(event.getContent() + System.getProperty("line.separator"));
				osw.flush();
				
				fos.close();
				osw.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("[RECORD EVENT HANDLER ERROR]", e);
			} finally {
				logger.debug("[RECORD EVENT HANDLER DONE]");
				event.clear();
			}
		}
	}
}
