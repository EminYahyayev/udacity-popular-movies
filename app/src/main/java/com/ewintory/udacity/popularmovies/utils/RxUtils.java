package com.ewintory.udacity.popularmovies.utils;


import com.ewintory.udacity.popularmovies.data.repository.result.PageResult;
import com.ewintory.udacity.popularmovies.ui.model.PagingModel;

import rx.Observable.Transformer;

/**
 * @author Emin Yahyayev
 */
public final class RxUtils {

    public static <T> Transformer<PageResult<T>, PagingModel<T>> pageResultToModel() {
        return pageResultToModel(PagingModel.<T>init(false));
    }

    public static <T> Transformer<PageResult<T>, PagingModel<T>> pageResultToModel(PagingModel<T> initial) {
        return results -> results.scan(initial, (model, result) -> {
            // Началась загрузка новой старницы, переводим в загрузку
            if (result.inFlight)
                return (result.page == 1) ? PagingModel.init(true) : model.inProgress();
            // Загрузились новая страница, добавляем данные
            if (result.items != null)
                return model.nextPage(result.items);
            // Во время загрузки произошла ошибка
            if (result.errorMessage != null)
                return model.failure(result.errorMessage);
            // Неизвестное состояние
            throw new IllegalArgumentException("Unknown result: " + result);
        });
    }

    private RxUtils() {
        throw new AssertionError("No instances");
    }
}
