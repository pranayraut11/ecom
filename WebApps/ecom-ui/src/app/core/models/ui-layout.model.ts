import { UiComponent } from './ui-component.model';

export interface UiLayout {
  id: string;
  name: string;
  displayName: string;
  components: string[];
  route: string;
  theme: string;
  active: boolean;
  sections?: {
    id: string;
    name: string;
    displayName: string;
    componentIds: string[];
    orderIndex: number;
    style?: { [key: string]: any };
    properties?: { [key: string]: any };
  }[];
  style?: { [key: string]: any };
  meta?: { [key: string]: any };
}
