package bio.knowledge.server.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {
	
	// todo: contravariance
		
	public static <T,R> List<R> map(Function<T, R> f, Collection<T> collection) {
		return collection.stream().map(f).collect(Collectors.toList());
	}
	
	public static <T> List<T> filter(Predicate<T> p, Collection<T> collection) {
		return collection.stream().filter(p).collect(Collectors.toList());
	}
	
//	public static <T> List<T> filter(Predicate<T> p, T[] array) {
//		return Arrays.stream(array).filter(p).collect(Collectors.toList());
//	}
	
	public static <T,R> List<R> map(Function<T, R> f, T[] array) {
		return Arrays.stream(array).map(f).collect(Collectors.toList());
	}
	
	public static <T,R> List<R> flatmapList(Function<T,Collection<R>> f, Collection<T> collection) {
		return collection.stream().map(f).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public static <T,R> List<R> flatmap(Function<T,R[]> f, List<T> list) {
		return list.stream().map(f).flatMap(Arrays::stream).collect(Collectors.toList());
	}
	
	public static <T,R> List<R> flatmap(Function<T,R[]> f, T[] array) {
		return Arrays.stream(array).map(f).flatMap(Arrays::stream).collect(Collectors.toList());
	}
	
//	public static <T,U,R> Function<U, R> curry(BiFunction<T, U, R> f, T arg) {
//		return x -> f.apply(arg, x);
//	}
	
	public static <T,U,R> Function<T, R> curryRight(BiFunction<T, U, R> f, U arg) {
		return x -> f.apply(x, arg);
	}
	
}
