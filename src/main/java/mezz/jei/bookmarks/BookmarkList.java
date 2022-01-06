package mezz.jei.bookmarks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.config.BookmarkConfig;
import mezz.jei.gui.overlay.IIngredientGridSource;
import mezz.jei.ingredients.IngredientManager;

public class BookmarkList implements IIngredientGridSource {
	private final List<Object> list = new LinkedList<>();
	private final IngredientManager ingredientManager;
	private final BookmarkConfig bookmarkConfig;
	private final List<IIngredientGridSource.Listener> listeners = new ArrayList<>();

	public BookmarkList(IngredientManager ingredientManager, BookmarkConfig bookmarkConfig) {
		this.ingredientManager = ingredientManager;
		this.bookmarkConfig = bookmarkConfig;
	}

	public <T> boolean add(T ingredient) {
		IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredient);
		Object normalized = ingredientHelper.normalizeIngredient(ingredient);
		if (!contains(normalized)) {
			if (addToList(normalized, true)) {
				notifyListenersOfChange();
				bookmarkConfig.saveBookmarks(ingredientManager, list);
				return true;
			}
		}
		return false;
	}

	private boolean contains(Object ingredient) {
		// We cannot assume that ingredients have a working equals() implementation. Even ItemStack doesn't have one...
		IIngredientHelper<Object> ingredientHelper = ingredientManager.getIngredientHelper(ingredient);
		for (Object existing : list) {
			if (ingredient == existing) {
				return true;
			}
			if (existing != null && existing.getClass() == ingredient.getClass()) {
				if (equalUids(ingredientHelper, existing, ingredient)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean equalUids(IIngredientHelper<Object> ingredientHelper, Object a, Object b) {
		String uidA = ingredientHelper.getUniqueId(a, UidContext.Ingredient);
		String uidB = ingredientHelper.getUniqueId(b, UidContext.Ingredient);
		return uidA.equals(uidB);
	}

	public boolean remove(Object ingredient) {
		int index = 0;
		for (Object existing : list) {
			if (ingredient == existing) {
				list.remove(index);
				notifyListenersOfChange();
				bookmarkConfig.saveBookmarks(ingredientManager, list);
				return true;
			}
			index++;
		}
		return false;
	}

	public <T> boolean addToList(T ingredient, boolean addToFront) {
		if (addToFront) {
			list.add(0, ingredient);
		} else {
			list.add(ingredient);
		}
		return true;
	}

	@Override
	public List<?> getIngredientList(String filterText) {
		return list;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void addListener(IIngredientGridSource.Listener listener) {
		listeners.add(listener);
	}

	public void notifyListenersOfChange() {
		for (IIngredientGridSource.Listener listener : listeners) {
			listener.onChange();
		}
	}
}
