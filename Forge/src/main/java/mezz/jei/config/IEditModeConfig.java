package mezz.jei.config;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.core.config.IngredientBlacklistType;
import mezz.jei.ingredients.IngredientFilter;

public interface IEditModeConfig {
	<V> boolean isIngredientOnConfigBlacklist(ITypedIngredient<V> ingredient, IIngredientHelper<V> ingredientHelper);

	<V> void addIngredientToConfigBlacklist(IngredientFilter ingredientFilter, ITypedIngredient<V> ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper);

	<V> void removeIngredientFromConfigBlacklist(IngredientFilter ingredientFilter, ITypedIngredient<V> ingredient, IngredientBlacklistType blacklistType, IIngredientHelper<V> ingredientHelper);
}
