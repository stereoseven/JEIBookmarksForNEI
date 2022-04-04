package mezz.jei.ingredients;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.ingredients.IngredientInfo;
import mezz.jei.common.ingredients.RegisteredIngredients;
import mezz.jei.gui.ingredients.IListElement;
import net.minecraft.core.NonNullList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class IngredientListElementFactory {
	private static final Logger LOGGER = LogManager.getLogger();
	private static int ingredientAddedIndex = 0;

	private IngredientListElementFactory() {
	}

	public static NonNullList<IListElement<?>> createBaseList(RegisteredIngredients registeredIngredients) {
		NonNullList<IListElement<?>> ingredientListElements = NonNullList.create();

		for (IIngredientType<?> ingredientType : registeredIngredients.getIngredientTypes()) {
			addToBaseList(ingredientListElements, registeredIngredients, ingredientType);
		}

		return ingredientListElements;
	}

	public static <V> List<IListElement<V>> createList(RegisteredIngredients registeredIngredients, IIngredientType<V> ingredientType, Collection<V> ingredients) {
		return ingredients.stream()
			.map(i -> TypedIngredient.createTyped(registeredIngredients, ingredientType, i))
			.flatMap(Optional::stream)
			.map(IngredientListElementFactory::createOrderedElement)
			.toList();
	}

	public static <V> IListElement<V> createOrderedElement(ITypedIngredient<V> typedIngredient) {
		int orderIndex = ingredientAddedIndex++;
		return new ListElement<>(typedIngredient, orderIndex);
	}

	private static <V> void addToBaseList(NonNullList<IListElement<?>> baseList, RegisteredIngredients registeredIngredients, IIngredientType<V> ingredientType) {
		IngredientInfo<V> ingredientInfo = registeredIngredients.getIngredientInfo(ingredientType);
		Collection<V> ingredients = ingredientInfo.getAllIngredients();
		LOGGER.debug("Registering ingredients: " + ingredientType.getIngredientClass().getSimpleName());
		ingredients.stream()
			.map(i -> TypedIngredient.createTyped(registeredIngredients, ingredientType, i))
			.flatMap(Optional::stream)
			.map(IngredientListElementFactory::createOrderedElement)
			.forEach(baseList::add);
	}

}
