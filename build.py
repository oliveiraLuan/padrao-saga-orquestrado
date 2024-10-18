import os
import threading

threads = []

def build_application(app):
    threads.append(app)
    print("Construindo aplicação {}".format(app))
    os.system("cd {} && gradle build -x test".format(app))
    print("Aplicação {} finalizou a construção".format(app))
    threads.remove(app)

def docker_compose_up():
    print("Rodando containers!")
    os.popen("docker-compose up --build -d").read()
    print("Pipeline finalizada")

def build_all_applications():
    print("Iniciando build das apps")
    threading.Thread(target=build_application, args={"order-service"}).start()
    threading.Thread(target=build_application, args={"orchestrator-service"}).start()
    threading.Thread(target=build_application, args={"product-validation-service"}).start()
    threading.Thread(target=build_application, args={"payment-service"}).start()
    threading.Thread(target=build_application, args={"inventory-service"}).start()

def removing_remaining_containers():
    print("Removendo todos os containers")
    os.system("docker-compose down")
    containers = os.popen('docker ps -aq').read().split('\n')
    containers.remove('')
    if len(containers) > 0:
        print("Ainda há {} containers criado".format(containers))
        for container in containers:
            print("Finalizando container {}".format(container))
                os.system("docker container stop {}".format(container))
            os.system("docker container prune -f")
if __name__ == "__main__":
    print("Pipeline iniciada!")
    build_all_applications()
    while len(threads) > 0:
        pass
    removing_remaining_containers()
    threading.Thread(target=docker_compose_up).start()