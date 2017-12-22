package jp.ats.substrate.concurrent;

/**
 * ServiceHandler を生成するファクトリです。
 *
 * @author 千葉 哲嗣
 */
public interface IOStreamServiceHandlerFactory {

	IOStreamServiceHandler newInstance();
}
