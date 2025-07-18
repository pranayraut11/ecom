export interface UiComponent {
  id: string;
  name: string;
  displayName: string;
  componentType: string;
  properties: { [key: string]: any };
  style?: { [key: string]: any };
  orderIndex: number;
  active: boolean;
  children?: UiComponent[];
  data?: { [key: string]: any };
  meta?: { [key: string]: any };
}
