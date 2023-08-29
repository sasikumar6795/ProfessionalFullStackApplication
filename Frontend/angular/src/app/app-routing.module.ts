import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import * as path from "path";
import {CustomerComponent} from "./components/customer/customer.component";
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  {
    path: 'customers',
    component: CustomerComponent
  },
  {
    path: 'login',
    component: LoginComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
