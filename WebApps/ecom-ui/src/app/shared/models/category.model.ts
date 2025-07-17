import { SubCategory } from "./SubCategory.model"

export class Category {
    id: string;
    name: string;
    description: string;
    imageUrl: string;
    prefix: string;
    amount: string;
    subCategories: SubCategory[];

	constructor(id: string, name: string, description: string, imageUrl: string, prefix: string, amount: string, subCategories: SubCategory[]) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.prefix = prefix;
		this.amount = amount;
		this.subCategories = subCategories;
	}
}