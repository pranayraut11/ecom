export class SubMenu {
    name: string
    url: string
    imageUrl?: string
    subCategories: SubMenu[]
    
    constructor(name: string, url: string, subCategories: SubMenu[] = [], imageUrl?: string) {
        this.name = name
        this.url = url
        this.subCategories = subCategories
        this.imageUrl = imageUrl
    }
}