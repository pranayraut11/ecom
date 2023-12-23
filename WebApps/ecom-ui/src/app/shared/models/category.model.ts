import { SubCategory } from "./SubCategory.model"

export class Category {

    private name: string;
    private subCategories: SubCategory[];


	constructor($name: string, $subCategories: SubCategory[]) {
		this.name = $name;
		this.subCategories = $subCategories;
	}

}