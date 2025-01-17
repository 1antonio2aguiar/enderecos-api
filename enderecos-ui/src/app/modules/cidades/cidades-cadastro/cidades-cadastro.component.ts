import { MessageService } from "primeng/api";
import { Cidades } from "./../../../shared/models/cidades";
import { Component, Injector } from "@angular/core";
import { BaseResourceFormComponent } from "../../../shared/components/base-resource-form/base-resource-form.component";
import { CidadesService } from "../cidades.service";
import { Validators } from "@angular/forms";
import { EstadosService } from "./../../estados/estados.service";

@Component({
  selector: "app-cidades-cadastro",
  templateUrl: "./cidades-cadastro.component.html",
  styleUrls: ["./cidades-cadastro.component.css"],
})
export class CidadesCadastroComponent extends BaseResourceFormComponent<Cidades> {
  estadoList = [];

  constructor(
    protected cidadesService: CidadesService,
    protected estadosService: EstadosService,
    protected injector: Injector,
    public messageService: MessageService
  ) {
    super(injector, new Cidades(), cidadesService, Cidades.fromJson, messageService);
    this.loadEstado();
  }

  //AA
  protected buildResourceForm() {
    this.resourceForm = this.formBuilder.group({
      codigoIbge: [null],
      codigoInep: [null],
      codigoSicom: [null],
      id: [null],
      nome: [null, [Validators.required, Validators.minLength(5)]],
      sigla: [null],
      estadosId: [null],
      usuario: [null],
    });

    this.resourceForm.patchValue({
      usuario: JSON.parse(sessionStorage.getItem("usuario")).nome,
    });

    //console.log(this.resourceForm);
  }

  protected creationPageTitle(): string {
    return "Cadastro de Nova Cidade";
  }

  protected editionPageTitle(): string {
    const cidadesName = this.resource.nome;
    return "Editando Cidade: " + cidadesName;
  }

  loadEstado() {
    this.estadosService
      .listAll()
      .then((estados) => {
        this.estadoList = estados.map((c) => ({ label: c.nome, value: c.id }));
      })
      .catch((erro) => erro);
  }

  //AA
  numberOnly(event): boolean {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }

  //aa
  protected createResource() {
    const resource: Cidades = Cidades.toJson(this.resourceForm.value);

    this.resourceService
      .create(resource)
      // tslint:disable-next-line:no-shadowed-variable
      .subscribe(
        (result) => {
          this.actionsForSuccess(result);
        },
        (error) => this.actionsForError(error)
      );
  }

  //aa
  protected updateResource() {
    const resource: Cidades = Cidades.toJson(this.resourceForm.value);
    this.resourceService.update(resource).subscribe(
      // tslint:disable-next-line:no-shadowed-variable
      (resource) => {
        this.actionsForSuccess(resource);
      },
      (error) => this.actionsForError(error)
    );
  }

  /*
  protected actionsForSuccesso() {
    console.log("call Sucess");

    // toastr.success('Solicitação processado com sucesso');
    this.messageService.add({
      severity: "success",
      summary: "Sucesso!",
      detail: "Solicitação processado com sucesso",
    });
    const baseComponentPath = this.route.snapshot.parent.url[0].path;
    this.router.navigateByUrl(baseComponentPath, { skipLocationChange: true }).then(() => {
      console.log(this.router);
      return this.router.navigate(["/" + baseComponentPath]);
    });
  }
  */
}
