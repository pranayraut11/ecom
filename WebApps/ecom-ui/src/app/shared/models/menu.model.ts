import { SubMenu } from "./subMenu.model"

export class Menu {

    category: string
    subCategories: SubMenu[]

    constructor(category: string, subCategories: SubMenu[]) {
        this.category = category
        this.subCategories = subCategories
    }

}