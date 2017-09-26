package com.crazysunj.domain.interactor.weather;

import com.crazysunj.domain.constant.CodeConstant;
import com.crazysunj.domain.entity.WeatherRemoteEntity;
import com.crazysunj.domain.exception.ApiException;
import com.crazysunj.domain.interactor.UseCase;
import com.crazysunj.domain.repository.weather.WeatherRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * author: sunjian
 * created on: 2017/9/5 下午5:34
 * description:
 */

public class WeatherUseCase extends UseCase<List<WeatherRemoteEntity.WeatherEntity>, WeatherUseCase.Params> {

    private final WeatherRepository mWeatherRepository;

    @Inject
    public WeatherUseCase(WeatherRepository weatherRepository) {
        mWeatherRepository = weatherRepository;
    }

    @Override
    protected Flowable<List<WeatherRemoteEntity.WeatherEntity>> buildUseCaseObservable(Params params) {
        return mWeatherRepository.getWeatherList(params.city, params.language)
                .flatMap(weatherRemoteEntity -> {
                    if (weatherRemoteEntity == null) {
                        return Flowable.error(new ApiException(CodeConstant.CODE_EMPTY, "数据为空，请求个毛线！"));
                    }
                    if (!CodeConstant.CODE_OK.equals(weatherRemoteEntity.getStatus())) {
                        return Flowable.error(new ApiException(CodeConstant.CODE_DATA_ERROR, "数据错误，没法快乐玩耍！"));
                    }
                    List<WeatherRemoteEntity.WeatherEntity> weather = weatherRemoteEntity.getWeather();
                    if (weather == null || weather.isEmpty()) {
                        return Flowable.error(new ApiException(CodeConstant.CODE_EMPTY, "数据为空，请求个毛线！"));
                    }
                    return Flowable.just(weather);
                });
    }


    public static final class Params {

        private static final String DEFAULT_LANGUAGE = "zh-chs";

        private final String city;
        private final String language;

        private Params(String city, String language) {
            this.city = city;
            this.language = language;
        }

        public static Params get(String city) {
            return new Params(city, DEFAULT_LANGUAGE);
        }
    }
}
