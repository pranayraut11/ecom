export class SubMenu {

    subCategory: string
    url: string
    subCategories: SubMenu[]
    constructor(subCategory: string,
        url: string, subCategories: SubMenu[]) {
        this.subCategory = subCategory
        this.url = url
        this.subCategories = subCategories

    }
}