<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gráficos de Municipios</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
            color: #333;
        }

        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            max-width: 900px;
            margin: 0 auto;
            display: flex;
            flex-direction: column;
            align-items: center;
            overflow-x: auto;
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }

        .chart-container {
            width: 95%;
            margin-bottom: 30px;
            min-width: 600px;
            height: 450px;
        }

        canvas {
            max-width: 100%;
            height: 100%;
        }

        .button-container {
            text-align: center;
            margin-top: 20px;
        }

        .button-container a,
        .button-container button {
            display: inline-block;
            padding: 10px 15px;
            margin: 0 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
            text-decoration: none;
            color: white;
            transition: background-color 0.3s ease;
        }

        .back-button {
             background-color: #0275d8;
         }
         .back-button:hover {
             background-color: #025aa5;
         }

        .logout-button {
            background-color: #d9534f;
        }
        .logout-button:hover {
            background-color: #c9302c;
        }

        .download-button {
             background-color: #f0ad4e;
         }
         .download-button:hover {
             background-color: #ec971f;
         }


         .message {
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 4px;
            font-weight: bold;
            text-align: center;
        }
        .success-message {
            color: #3c763d;
            background-color: #dff0d8;
            border: 1px solid #d6e9c6;
        }
         .error-message {
            color: #a94442;
            background-color: #f2dede;
            border: 1px solid #ebccd1;
        }

    </style>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
</head>
<body>
    <div class="container">
        <h1>Datos Generales por Municipio</h1>

        <div class="chart-container">
            <h2>Resumen General de Municipios</h2>
            <canvas id="generalChart"></canvas>
        </div>

        <div class="button-container">
            <a href="home" class="back-button">Volver al Listado</a>

            <button onclick="downloadChartAsPDF()" class="download-button">Descargar Gráfico como PDF</button>

            <button onclick="location.href='logout'" class="logout-button">Cerrar Sesión</button>
        </div>

    </div>

    <script>
        var municipios = [];
        <c:forEach var="municipio" items="${municipios}">
            municipios.push({
                id: <c:out value="${municipio.id}"/>,
                nombre: "<c:out value="${municipio.nombre}"/>",
                departamento: "<c:out value="${municipio.departamento}"/>",
                pais: "<c:out value="${municipio.pais}"/>",
                alcalde: "<c:out value="${municipio.alcalde}"/>",
                gobernador: "<c:out value="${municipio.gobernador}"/>",
                patronoReligioso: "<c:out value="${municipio.patronoReligioso}"/>",
                numHabitantes: <c:out value="${municipio.numHabitantes != null ? municipio.numHabitantes : 0}"/>,
                numCasas: <c:out value="${municipio.numCasas != null ? municipio.numCasas : 0}"/>,
                numParques: <c:out value="${municipio.numParques != null ? municipio.numParques : 0}"/>,
                numColegios: <c:out value="${municipio.numColegios != null ? municipio.numColegios : 0}"/>,
                descripcion: "<c:out value="${municipio.descripcion}"/>"
            });
        </c:forEach>

        console.log("Datos de municipios cargados para gráficos:", municipios);

        var nombresMunicipios = municipios.map(function(m) { return m.nombre; });

        var habitantesData = municipios.map(function(m) { return m.numHabitantes; });
        var casasData = municipios.map(function(m) { return m.numCasas; });
        var colegiosData = municipios.map(function(m) { return m.numColegios; });
        var parquesData = municipios.map(function(m) { return m.numParques; });


        var ctx = document.getElementById('generalChart').getContext('2d');
        var generalChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: nombresMunicipios,
                datasets: [
                    {
                        label: 'Habitantes',
                        data: habitantesData,
                        backgroundColor: 'rgba(75, 192, 192, 0.7)',
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 1
                    },
                    {
                        label: 'Casas',
                        data: casasData,
                        backgroundColor: 'rgba(255, 205, 86, 0.7)',
                        borderColor: 'rgba(255, 205, 86, 1)',
                        borderWidth: 1
                    },
                    {
                        label: 'Colegios',
                        data: colegiosData,
                        backgroundColor: 'rgba(153, 102, 255, 0.7)',
                        borderColor: 'rgba(153, 102, 255, 1)',
                        borderWidth: 1
                    },
                    {
                        label: 'Parques',
                        data: parquesData,
                        backgroundColor: 'rgba(255, 99, 132, 0.7)',
                        borderColor: 'rgba(255, 99, 132, 1)',
                        borderWidth: 1
                    }
                ]
            },
            options: {
                 scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Cantidad'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: 'Municipio'
                        },
                        ticks: {
                            autoSkip: false
                        },
                        categoryPercentage: 0.8,
                        barPercentage: 0.9,
                         barThickness: 'flex',
                         maxBarThickness: 50
                    }
                },
                responsive: true,
                maintainAspectRatio: false,
                 plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Resumen General de Municipios'
                    }
                }
            }
        });


        function downloadChartAsPDF() {
            const canvas = document.getElementById('generalChart');

            const chartImage = canvas.toDataURL('image/png', 1.0);

            const pdf = new window.jspdf.jsPDF('p', 'mm', 'a4');

            let yOffset = 20;
            const margin = 15;

            pdf.setFontSize(16);
            pdf.text("Resumen de Datos de Municipios", margin, yOffset);
            yOffset += 10;

            pdf.setFontSize(12);
            let descriptionText = "Este gráfico muestra un resumen de datos clave (Habitantes, Casas, Colegios, Parques) para cada municipio registrado:\n\n";

            municipios.forEach(function(m) {
                descriptionText += `- ${m.nombre}: Habitantes: ${m.numHabitantes}, Casas: ${m.numCasas}, Colegios: ${m.numColegios}, Parques: ${m.numParques}.\n`;
            });

            const splitText = pdf.splitTextToSize(descriptionText, 210 - 2 * margin);
            pdf.text(splitText, margin, yOffset);

            yOffset += (splitText.length * pdf.getLineHeight()) + 10;

            const imgWidth = canvas.width;
            const imgHeight = canvas.height;
            const ratio = imgHeight / imgWidth;

            const pdfWidth = 180;
            const pdfHeight = pdfWidth * ratio;

            const remainingHeight = pdf.internal.pageSize.height - yOffset - margin;
            if (pdfHeight > remainingHeight) {
                pdf.addPage();
                yOffset = margin;
            }

            const x = (210 - pdfWidth) / 2;
            pdf.addImage(chartImage, 'PNG', x, yOffset, pdfWidth, pdfHeight);

            pdf.save('grafico_municipios.pdf');
        }

    </script>
</body>
</html>
