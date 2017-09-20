package com.genitus.channel.tracker.util.audio;

import com.iflytek.audio.WavWriter;
import com.iflytek.dm.lib.Amr;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
public class AudioTransform {
	//private static final Logger LOGGER = LoggerFactory.getLogger(AudioTransform.class);

	public AudioTransform() {
	}

	/**
	 * transform origin byte[] as return byte[] bt
	 * @param origin
	 * @param aue
	 * @param auf
	 * @return
	 */
	public static byte[] getSoundByte(byte[] origin, String aue, String auf) {
		byte[] bt = null;
		if (StringUtils.isNotBlank(aue) && ArrayUtils.isNotEmpty(origin)) {
			aue = StringUtils.trim(aue);
			aue = aue.toLowerCase();
			if (!aue.startsWith("speex") && !aue.startsWith("amr")) {
				if (aue.startsWith("feature")) {
					return origin;
				}

				if (aue.startsWith("raw")) {
					if (StringUtils.isNotBlank(auf) && auf.indexOf("16000") > 0) {
						bt = WavWriter.addWavHeader(bt, 16000);
					} else if (StringUtils.isNotBlank(auf) && auf.indexOf("8000") > 0) {
						bt = WavWriter.addWavHeader(bt, 8000);
					} else {
						bt = WavWriter.addWavHeader(bt, 16000);
					}
				}
			} else {
				bt = Amr.getInstance().Decode(origin, aue);
				if (StringUtils.isNotBlank(auf) && auf.indexOf("16000") > 0) {
			//		LOGGER.info("16K invoke................");
					bt = WavWriter.addWavHeader(bt, 16000);
				} else if (StringUtils.isNotBlank(auf) && auf.indexOf("8000") > 0) {
			//		LOGGER.info("8K invoke................");
					bt = WavWriter.addWavHeader(bt, 8000);
				} else {
			//		LOGGER.info("else 8K invoke................");
					bt = WavWriter.addWavHeader(bt, 16000);
				}

			//	LOGGER.info(aue);
			}
		}

		if (bt == null) {
		//	LOGGER.info("sound byte[]  == null");
		}

		return bt;
	}
}
